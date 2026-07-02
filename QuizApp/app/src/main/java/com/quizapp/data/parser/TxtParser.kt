package com.quizapp.data.parser

class TxtParser : QuestionParser {

    override fun parse(content: String): List<ParsedQuestion> {
        val lines = content.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val questions = mutableListOf<ParsedQuestion>()
        var i = 0

        while (i < lines.size) {
            val line = lines[i]

            // Match question start: "数字. " or "数字、" or "# 数字."
            val questionMatch = Regex("""^(?:#+\s*)?(\d+)[.、．,，]\s*(.*)""").find(line)
            if (questionMatch != null) {
                val questionContent = questionMatch.groupValues[2].trim()

                // Detect embedded judge answer: "（对）" or "（错）" at end
                val embeddedJudge = Regex("""[（(]\s*(对|错|正确|错误)\s*[）)]\s*$""").find(questionContent)
                var embeddedAnswer = ""
                if (embeddedJudge != null) {
                    val ans = embeddedJudge.groupValues[1]
                    embeddedAnswer = if (ans == "对" || ans == "正确") "正确" else "错误"
                }
                val options = mutableListOf<String>()
                var answer = ""
                var analysis = ""
                var questionType = "SINGLE"
                i++

                // Collect options — handle multiple formats
                while (i < lines.size) {
                    val optLine = lines[i]
                    // Format 1: "• A. xxx" or "• A、xxx" or "- A. xxx" or "* A. xxx"
                    // Format 2: "A. xxx" or "A、xxx" or "A) xxx"
                    val optMatch = Regex("""^[•·\-*\s]*([A-Za-z])[.、．)]\s*(.*)""").find(optLine)
                    if (optMatch != null) {
                        val letter = optMatch.groupValues[1]
                        val text = optMatch.groupValues[2].trim()
                        // Skip if this looks like a new question number
                        if (Regex("""^\d+[.、]""").matches(text)) break
                        options.add("$letter. $text")
                        i++
                    } else {
                        break
                    }
                }

                // Read answer and analysis
                while (i < lines.size) {
                    val detailLine = lines[i]
                    when {
                        detailLine.startsWith("答案", ignoreCase = true) || detailLine.startsWith("参考答案", ignoreCase = true) -> {
                            answer = detailLine.replace(Regex("""(?:参考)?答案[:：]\s*"""), "").trim()
                                .trim('"', '“', '”', '‘', '’', '\'', '。', '.')
                            i++
                        }
                        detailLine.startsWith("正确的答案是", ignoreCase = true) -> {
                            answer = detailLine.replace(Regex("""正确的答案是[：:]\s*"""), "").trim()
                                .trim('"', '“', '”', '‘', '’', '\'', '。', '.')
                            i++
                        }
                        detailLine.startsWith("正确答案是", ignoreCase = true) || detailLine.startsWith("正确答案为", ignoreCase = true) -> {
                            answer = detailLine.replace(Regex("""正确答案[是为][：:"]?\s*"""), "").trim()
                                .trim('"', '“', '”', '‘', '’', '\'', '。', '.')
                            i++
                        }
                        detailLine.startsWith("解析", ignoreCase = true) -> {
                            analysis = detailLine.replace(Regex("""解析[:：]\s*"""), "").trim()
                            i++
                        }
                        detailLine.contains("**答案**") || detailLine.contains("__答案__") -> {
                            if (answer.isEmpty()) answer = detailLine.replace(Regex("""\*{0,2}_{0,2}答案\*{0,2}_{0,2}[:：]?\s*"""), "").trim()
                            i++
                        }
                        detailLine.contains("**解析**") || detailLine.contains("__解析__") -> {
                            if (analysis.isEmpty()) analysis = detailLine.replace(Regex("""\*{0,2}_{0,2}解析\*{0,2}_{0,2}[:：]?\s*"""), "").trim()
                            i++
                        }
                        else -> break
                    }
                }

                // Determine question type
                // Normalize "对"/"错" → "正确"/"错误" first
                if (answer == "对") answer = "正确"
                if (answer == "错") answer = "错误"

                // Use embedded answer as fallback
                if (answer.isEmpty() && embeddedAnswer.isNotEmpty()) {
                    answer = embeddedAnswer
                }

                if (options.isEmpty()) {
                    questionType = if (answer == "正确" || answer == "错误") "JUDGE" else "SINGLE"
                }
                // Check if options are "A. 正确 / B. 错误" -> JUDGE
                val optionTexts = options.map { it.substringAfter(". ").trim() }
                if (optionTexts.size == 2 && optionTexts.containsAll(listOf("正确", "错误"))) {
                    questionType = "JUDGE"
                }
                // Detect FILL type: has blank markers, no options, text answer
                if (options.isEmpty() && answer.isNotEmpty() &&
                    answer != "正确" && answer != "错误" &&
                    !answer.all { it in 'A'..'Z' } &&
                    (questionContent.contains("_____") || questionContent.contains("____") ||
                     questionContent.contains("___") || questionContent.contains("（）") ||
                     questionContent.contains("( )") || questionContent.contains("（  ）"))) {
                    questionType = "FILL"
                }
                if (answer.length > 1 && !answer.contains("正确") && !answer.contains("错误") && questionType != "JUDGE" && questionType != "FILL") {
                    questionType = "MULTI"
                }

                // For JUDGE questions without options, add standard 正确/错误 options
                if (questionType == "JUDGE" && options.isEmpty()) {
                    options.add("A. 正确")
                    options.add("B. 错误")
                }

                // Normalize answer format
                when {
                    questionType == "MULTI" -> {
                        // "A, C" → "AC", "A, C, D" → "ACD"
                        answer = answer.replace(Regex("""[\s,，、]+"""), "")
                            .uppercase().toCharArray().sorted().joinToString("")
                    }
                    questionType == "JUDGE" && options.size == 2 -> {
                        // Map A→"正确", B→"错误" for judge questions with options
                        val optMap = options.associate {
                            it.substringBefore(".").trim().uppercase() to it.substringAfter(". ").trim()
                        }
                        if (optMap.values.containsAll(listOf("正确", "错误"))) {
                            answer = optMap[answer.trim().uppercase()] ?: answer
                        }
                        // Normalize "对"/"错" → "正确"/"错误"
                        if (answer == "对") answer = "正确"
                        if (answer == "错") answer = "错误"
                    }
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
            } else {
                i++
            }
        }

        return questions
    }
}
