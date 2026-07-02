package com.quizapp.data.parser

import android.content.Context
import android.net.Uri
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.io.StringReader
import java.util.zip.ZipInputStream

class DocxParser(private val context: Context) : QuestionParser {

    override fun parse(content: String): List<ParsedQuestion> {
        return emptyList()
    }

    fun parseFromUri(uri: Uri): List<ParsedQuestion> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return emptyList()
        return parseFromStream(inputStream)
    }

    /**
     * Parse .docx from an InputStream (for both Uri-based and asset-based import).
     */
    fun parseFromStream(inputStream: InputStream): List<ParsedQuestion> {
        return inputStream.use { stream ->
            val zip = ZipInputStream(stream)
            var documentXml: String? = null

            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == "word/document.xml") {
                    documentXml = zip.bufferedReader().readText()
                    break
                }
                entry = zip.nextEntry
            }

            if (documentXml == null) return@use emptyList()

            val paragraphs = extractParagraphs(documentXml)
            parseQuestions(paragraphs)
        }
    }

    /**
     * A paragraph extracted from .docx XML with its text and yellow-highlight flag.
     */
    private data class DocxParagraph(
        val text: String,
        val hasYellow: Boolean
    )

    /**
     * Extract all paragraphs from document.xml with formatting info.
     */
    private fun extractParagraphs(xml: String): List<DocxParagraph> {
        val result = mutableListOf<DocxParagraph>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var currentText = StringBuilder()
            var currentHasYellow = false
            var inParagraph = false
            var inTextRun = false
            var runHasYellow = false
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "p" -> {
                                inParagraph = true
                                currentText = StringBuilder()
                                currentHasYellow = false
                            }
                            "r" -> {
                                runHasYellow = false
                            }
                            "highlight" -> {
                                if (parser.getAttributeValue(null, "val") == "yellow") {
                                    runHasYellow = true
                                    currentHasYellow = true
                                }
                            }
                            "t" -> if (inParagraph) inTextRun = true
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        when (parser.name) {
                            "p" -> {
                                val text = currentText.toString().trim()
                                if (text.isNotEmpty()) {
                                    result.add(DocxParagraph(text, currentHasYellow))
                                }
                                inParagraph = false
                            }
                            "r" -> runHasYellow = false
                            "t" -> inTextRun = false
                        }
                    }
                    XmlPullParser.TEXT -> {
                        if (inTextRun) {
                            currentText.append(parser.text)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (_: Exception) { }
        return result
    }

    /**
     * Parse paragraphs into questions.
     *
     * Question start detection (in priority order):
     * Format A: "N、题目内容" — number + delimiter
     * Format B: "试题 N" header → next paragraph = question content
     * Format C: "N 单选/多选/判断 题目内容" — number + type label
     * Format D: Any paragraph that is NOT an option/answer/analysis and is
     *           followed by options — treated as a prefix-less question.
     *
     * Answer priority: explicit "正确答案" text > yellow highlighting.
     */
    private fun parseQuestions(paragraphs: List<DocxParagraph>): List<ParsedQuestion> {
        val questions = mutableListOf<ParsedQuestion>()
        var i = 0

        val qRegexA = Regex("""^(\d+)\s*[、．,.)）]\s*(.*)""")
        val qRegexC = Regex("""^(\d+)\s*(单选|多选|判断)\s*(.*)""")
        val sectionRegex = Regex("""^(?:试题|题号)\s*\d+""")
        val optLookahead = Regex("""^\s*[A-Za-z][.、．):]""")
        val answerRegex = Regex("""^(?:(?:正确|参考)答案[是为]?[：:。、]?|正确的答案是[：:。、]?)\s*(.*)""")

        while (i < paragraphs.size) {
            val text = paragraphs[i].text

            // ── Detect question start ──
            val qMatchA = qRegexA.find(text)
            val qMatchC = qRegexC.find(text)
            val sectionMatch = sectionRegex.find(text)

            // Skip section headers like "三、多选题" or "二、选择题"
            if (qMatchA != null && qMatchC == null && sectionMatch == null &&
                Regex("""(?:选题|断题|空题|答题|择题)$""").containsMatchIn(
                    qMatchA.groupValues.getOrNull(2) ?: "")) {
                i++
                continue
            }

            // Format D: prefix-less question (text that could be a question,
            // not an option/answer, and followed by an option paragraph)
            val isPlainQuestion = qMatchA == null && qMatchC == null && sectionMatch == null &&
                !optLookahead.containsMatchIn(text) &&
                !answerRegex.containsMatchIn(text) &&
                text !in listOf("对", "错", "正确", "错误") &&
                !Regex("""^(解析|分析)[:：]""").containsMatchIn(text) &&
                i + 1 < paragraphs.size &&
                optLookahead.containsMatchIn(paragraphs[i + 1].text)

            if (qMatchA == null && qMatchC == null && sectionMatch == null && !isPlainQuestion) {
                i++
                continue
            }

            var questionContent: String
            val options = mutableListOf<String>()
            var answer = ""
            var analysis = ""
            var questionType = "SINGLE"
            var yellowAnswerLetter: String? = null
            var hasExplicitAnswer = false

            when {
                qMatchA != null -> {
                    questionContent = qMatchA.groupValues[2].trim()
                    i++
                    // Merge continuation paragraphs (not options/answers/judge)
                    while (i < paragraphs.size) {
                        val nextText = paragraphs[i].text
                        if (qRegexA.containsMatchIn(nextText) || qRegexC.containsMatchIn(nextText) ||
                            sectionRegex.containsMatchIn(nextText)) break
                        // Don't merge short answer-like text
                        val isShortAnswer = nextText.length <= 10 &&
                            !optLookahead.containsMatchIn(nextText) &&
                            !answerRegex.containsMatchIn(nextText) &&
                            nextText !in listOf("对", "错", "正确", "错误") &&
                            !nextText.contains("。") && !nextText.contains("？") && !nextText.contains("，")
                        if (isShortAnswer) break
                        if (!optLookahead.containsMatchIn(nextText) &&
                            !answerRegex.containsMatchIn(nextText) &&
                            nextText !in listOf("对", "错", "正确", "错误") &&
                            !Regex("""^(解析|分析)[:：]""").containsMatchIn(nextText) &&
                            !Regex("""^选择一""").containsMatchIn(nextText)) {
                            questionContent = "$questionContent\n$nextText"
                            i++
                        } else break
                    }
                }
                qMatchC != null -> {
                    val typeLabel = qMatchC.groupValues[2]
                    questionType = when (typeLabel) {
                        "单选" -> "SINGLE"
                        "多选" -> "MULTI"
                        "判断" -> "JUDGE"
                        else -> "SINGLE"
                    }
                    questionContent = qMatchC.groupValues[3].trim()
                    i++
                }
                sectionMatch != null -> {
                    i++
                    questionContent = ""
                    while (i < paragraphs.size) {
                        val nextText = paragraphs[i].text
                        if (qRegexA.containsMatchIn(nextText) || qRegexC.containsMatchIn(nextText) ||
                            sectionRegex.containsMatchIn(nextText)) break
                        if (!optLookahead.containsMatchIn(nextText) &&
                            !answerRegex.containsMatchIn(nextText) &&
                            nextText !in listOf("对", "错", "正确", "错误") &&
                            !Regex("""^(解析|分析)[:：]""").containsMatchIn(nextText)) {
                            questionContent = if (questionContent.isEmpty()) nextText
                            else "$questionContent\n$nextText"
                            i++
                        } else break
                    }
                }
                else -> {
                    // Format D: prefix-less question
                    questionContent = text
                    i++
                }
            }

            if (questionContent.isEmpty()) continue

            // ── Detect embedded judge answer: "（对）" / "（错）" at end of question ──
            val embeddedJudge = Regex("""[（(]\s*(对|错|正确|错误)\s*[）)]\s*$""").find(questionContent)
            if (embeddedJudge != null) {
                val ans = embeddedJudge.groupValues[1]
                answer = if (ans == "对" || ans == "正确") "正确" else "错误"
                questionType = "JUDGE"
                questionContent = questionContent.removeRange(embeddedJudge.range).trim()
            }

            // ── Collect options, answer, analysis ──
            while (i < paragraphs.size) {
                val paraText = paragraphs[i].text

                // Stop if we hit a new question
                if (qRegexA.containsMatchIn(paraText) || qRegexC.containsMatchIn(paraText) ||
                    sectionRegex.containsMatchIn(paraText)) break

                // Option paragraph
                val optMatch = Regex("""^\s*([A-Za-z])[.、．):]\s*(.*)""").find(paraText)
                if (optMatch != null) {
                    val letter = optMatch.groupValues[1].uppercase()
                    val optText = optMatch.groupValues[2].trim()
                    options.add("$letter. $optText")
                    if (paragraphs[i].hasYellow) yellowAnswerLetter = letter
                    i++
                    continue
                }

                // Explicit answer paragraph
                val ansMatch = answerRegex.find(paraText)
                if (ansMatch != null) {
                    val ansText = ansMatch.groupValues[1].trim()
                    if (ansText.isNotEmpty()) {
                        answer = resolveAnswer(ansText, options)
                        hasExplicitAnswer = true
                    }
                    i++
                    continue
                }

                // Judge option pair: "对" followed by "错" (or vice versa)
                // These are the two options of a judge question, not standalone answers
                if ((paraText == "对" || paraText == "正确") &&
                    i + 1 < paragraphs.size && paragraphs[i + 1].text in listOf("错", "错误")) {
                    options.add("A. 正确")
                    options.add("B. 错误")
                    questionType = "JUDGE"
                    i += 2
                    continue
                }
                if ((paraText == "错" || paraText == "错误") &&
                    i + 1 < paragraphs.size && paragraphs[i + 1].text in listOf("对", "正确")) {
                    options.add("A. 正确")
                    options.add("B. 错误")
                    questionType = "JUDGE"
                    i += 2
                    continue
                }

                // Standalone judge answer (no option pair, no explicit answer ahead)
                if (paraText == "对" || paraText == "正确") {
                    // Only consume as answer if no explicit answer paragraph follows
                    if (i + 1 < paragraphs.size && answerRegex.containsMatchIn(paragraphs[i + 1].text)) {
                        // There's an explicit answer next — skip this, let the explicit answer handle it
                        i++
                        continue
                    }
                    answer = "正确"
                    if (questionType == "SINGLE") questionType = "JUDGE"
                    i++
                    continue
                }
                if (paraText == "错" || paraText == "错误") {
                    if (i + 1 < paragraphs.size && answerRegex.containsMatchIn(paragraphs[i + 1].text)) {
                        i++
                        continue
                    }
                    answer = "错误"
                    if (questionType == "SINGLE") questionType = "JUDGE"
                    i++
                    continue
                }

                // Analysis
                val analysisMatch = Regex("""^(解析|分析)[:：]\s*(.*)""").find(paraText)
                if (analysisMatch != null) {
                    analysis = analysisMatch.groupValues[2].trim()
                    i++
                    continue
                }

                // Skip "选择一项/选择一项或多项" type indicators
                if (Regex("""^选择一""").containsMatchIn(paraText)) {
                    // Use indicator to set question type
                    if (paraText.contains("或多项")) questionType = "MULTI"
                    i++
                    continue
                }
                // Only for fragments that don't look like standalone questions
                if (options.isNotEmpty() &&
                    !optLookahead.containsMatchIn(paraText) &&
                    !answerRegex.containsMatchIn(paraText) &&
                    paraText !in listOf("对", "错", "正确", "错误") &&
                    !Regex("""^(解析|分析)[:：]""").containsMatchIn(paraText) &&
                    !Regex("""^选择一""").containsMatchIn(paraText) &&
                    !Regex("""^\d""").containsMatchIn(paraText) &&  // not a numbered question
                    paraText.length < 12 &&
                    !paraText.contains("（）") &&
                    !paraText.contains("( )") &&
                    !paraText.contains("____") &&
                    !paraText.endsWith("。") &&
                    !paraText.endsWith("？")) {
                    val lastIdx = options.size - 1
                    options[lastIdx] = options[lastIdx] + paraText
                    i++
                    continue
                }

                // Already have options + answer → this is likely a new prefix-less question
                if (options.isNotEmpty() && answer.isNotEmpty()) break

                // Already have options + this paragraph doesn't look like answer → new question
                if (options.isNotEmpty() &&
                    !optLookahead.containsMatchIn(paraText) &&
                    !answerRegex.containsMatchIn(paraText) &&
                    paraText !in listOf("对", "错", "正确", "错误")) {
                    // Could be a prefix-less question — check if followed by options
                    if (i + 1 < paragraphs.size && optLookahead.containsMatchIn(paragraphs[i + 1].text)) {
                        break
                    }
                }

                // Any other text: if no options yet and no answer → short answer
                if (options.isEmpty() && answer.isEmpty()) {
                    answer = paraText
                }
                i++
            }

            // ── Resolve answer ──
            // Explicit "正确答案" text takes priority over yellow highlighting
            if (!hasExplicitAnswer && yellowAnswerLetter != null) {
                answer = yellowAnswerLetter
            }

            // ── Determine question type ──
            if (questionType == "SINGLE") {
                if (options.isEmpty() && answer.isNotEmpty()) {
                    when (answer) {
                        "正确", "错误" -> questionType = "JUDGE"
                        "对" -> { answer = "正确"; questionType = "JUDGE" }
                        "错" -> { answer = "错误"; questionType = "JUDGE" }
                    }
                } else if (options.isNotEmpty()) {
                    val optionTexts = options.map { it.substringAfter(". ").trim() }
                    if (optionTexts.size == 2 &&
                        (optionTexts.containsAll(listOf("正确", "错误")) || optionTexts.containsAll(listOf("对", "错")))
                    ) {
                        questionType = "JUDGE"
                        if (answer == "对") answer = "正确"
                        if (answer == "错") answer = "错误"
                    }
                    // Multi-select: answer is multiple letters (A-Z only, sorted)
                    if (answer.length > 1 && questionType != "JUDGE" &&
                        answer.all { it in 'A'..'Z' }
                    ) {
                        questionType = "MULTI"
                        answer = answer.toCharArray().sorted().joinToString("")
                    }
                }
            }

            if (questionType == "JUDGE" && options.isEmpty()) {
                options.add("A. 正确")
                options.add("B. 错误")
            }

            // Fill-in-the-blank: has （） or ____ in content, no options, text answer
            if (questionType == "SINGLE" && options.isEmpty() && answer.isNotEmpty() &&
                answer !in listOf("正确", "错误", "对", "错") &&
                !answer.all { it in 'A'..'Z' } &&
                (questionContent.contains("（）") || questionContent.contains("( )") ||
                 questionContent.contains("____") || questionContent.contains("（  ）") ||
                 questionContent.contains("___") || questionContent.contains("_____"))) {
                questionType = "FILL"
            }

            questions.add(
                ParsedQuestion(
                    content = questionContent,
                    questionType = questionType,
                    options = options,
                    answer = answer,
                    analysis = analysis
                )
            )
        }

        return questions
    }

    /**
     * Resolve answer text from "正确答案是：XXX" to option letter(s).
     * "XXX" could be:
     * - A single letter like "A", "B" → use directly
     * - The full option text like "接口" → match to option letter
     * - Multiple answers like "A, B" or "ABC" → MULTI
     * - "对"/"错" → judge
     */
    private fun resolveAnswer(answerText: String, options: List<String>): String {
        // Strip quotes, colons, spaces, and trailing punctuation
        var trimmed = answerText.trim()
            .trimStart('"', '“', '”', '‘', '’', '\'', ':', '：', ' ')
            .trimEnd('"', '“', '”', '‘', '’', '\'', '。', '.', '，', ',', '、', '；', ';', ' ')

        // "对"/"错" → normalize
        if (trimmed == "对" || trimmed == "正确") return "正确"
        if (trimmed == "错" || trimmed == "错误") return "错误"

        // Single letter answer
        if (trimmed.length == 1 && trimmed[0] in 'A'..'Z') {
            return trimmed
        }

        // Multi-letter: "AB", "ACD" etc.
        if (trimmed.length > 1 && trimmed.all { it in 'A'..'Z' }) {
            return trimmed.toCharArray().sorted().joinToString("")
        }

        // Comma-separated: "A, B, C"
        val commaLetters = trimmed.split(Regex("""[\s,，、]+"""))
            .filter { it.length == 1 && it[0] in 'A'..'Z' }
        if (commaLetters.isNotEmpty() && commaLetters.size == trimmed.split(Regex("""[\s,，、]+""")).size) {
            return commaLetters.joinToString("")
        }

        // Try to match answer text to option content
        // First, check if the answer contains multiple option texts (multi-select)
        val matchedLetters = mutableListOf<String>()
        for (opt in options) {
            val optText = opt.substringAfter(". ").trim()
            if (optText.length > 2 && trimmed.contains(optText)) {
                matchedLetters.add(opt.substringBefore(".").trim())
            }
        }
        if (matchedLetters.isNotEmpty()) {
            return matchedLetters.joinToString("")
        }

        // Single exact match
        for (opt in options) {
            val optText = opt.substringAfter(". ").trim()
            if (optText == trimmed) {
                return opt.substringBefore(".").trim()
            }
        }

        // Fallback: return the raw text (for short-answer questions)
        return trimmed
    }
}
