package com.quizapp.data.parser

/**
 * 规范化题目数据：统一答案格式、选项格式、题目类型判定。
 * 在解析之后、校验之前执行，确保所有题目以一致的结构进入校验层。
 */
object QuestionNormalizer {

    fun normalize(question: ParsedQuestion): ParsedQuestion {
        var content = question.content.trim()
        var options = question.options.toMutableList()
        var answer = question.answer.trim()
        var type = question.questionType

        // ── 1. 标准化选项格式 ──
        // 确保所有选项都是 "A. xxx" 格式
        options = options.mapIndexed { idx, opt ->
            val trimmed = opt.trim()
            val letter = ('A'.toInt() + idx).toChar()
            // 如果选项已经是 "X. xxx" 格式，保留原字母
            val existingMatch = Regex("""^([A-Za-z])[.、．):]\s*(.*)""").find(trimmed)
            if (existingMatch != null) {
                val l = existingMatch.groupValues[1].uppercase()
                val t = existingMatch.groupValues[2].trim()
                "$l. $t"
            } else {
                "$letter. $trimmed"
            }
        }.toMutableList()

        // ── 2. 去除题目末尾嵌入的答案标记 ──
        // 比如 "题目内容（B）" 或 "题目内容（对）"
        val embeddedLetter = Regex("""\s*[（(]\s*([A-Za-z])\s*[）)]\s*$""").find(content)
        val embeddedJudge = Regex("""\s*[（(]\s*(对|错|正确|错误)\s*[）)]\s*$""").find(content)

        if (embeddedJudge != null && answer.isEmpty()) {
            val ans = embeddedJudge.groupValues[1]
            answer = if (ans == "对" || ans == "正确") "正确" else "错误"
            content = content.removeRange(embeddedJudge.range).trim()
        }
        if (embeddedLetter != null && answer.isEmpty()) {
            answer = embeddedLetter.groupValues[1].uppercase()
            content = content.removeRange(embeddedLetter.range).trim()
        }

        // ── 3. 统一答案格式 ──
        answer = normalizeAnswer(answer)

        // ── 4. 重新判定题目类型 ──
        type = detectType(content, options, answer, type)

        // ── 5. 判断题补充默认选项 ──
        if (type == "JUDGE" && options.isEmpty()) {
            options.add("A. 正确")
            options.add("B. 错误")
        }

        // ── 6. 多选题答案排序 ──
        if (type == "MULTI" && answer.all { it in 'A'..'Z' }) {
            answer = answer.toCharArray().sorted().joinToString("")
        }

        return ParsedQuestion(
            content = content,
            questionType = type,
            options = options,
            answer = answer,
            analysis = question.analysis.trim()
        )
    }

    /**
     * 规范化答案字符串：
     * - "对"/"错" → "正确"/"错误"
     * - "A, C" → "AC"
     * - 去除引号、标点等干扰字符
     */
    fun normalizeAnswer(raw: String): String {
        var s = raw.trim()
            .trim('"', '“', '”', '‘', '’', '\'', '「', '」')
            .trim('。', '.', '，', ',', '、', '；', ';', ' ', '：', ':')

        if (s.isEmpty()) return ""

        // 判断题归一
        if (s == "对") return "正确"
        if (s == "错") return "错误"
        if (s == "正确" || s == "错误") return s

        // 单字母答案
        if (s.length == 1 && s[0] in 'A'..'Z') return s

        // 多字母连写 "ACD"
        if (s.all { it in 'A'..'Z' }) return s

        // 逗号/空格/顿号分隔 "A, C, D"
        val parts = s.split(Regex("""[\s,，、;；]+"""))
            .map { it.trim().uppercase() }
            .filter { it.length == 1 && it[0] in 'A'..'Z' }
        if (parts.size >= 2) return parts.joinToString("")

        // 单个字母带标点 "A." → "A"
        val singleLetter = Regex("""^([A-Za-z])[.、．)]?$""").find(s)
        if (singleLetter != null) return singleLetter.groupValues[1].uppercase()

        return s
    }

    /**
     * 根据题目内容、选项、答案重新判定题目类型。
     * 优先信任解析器传人的初始类型，但会根据实际数据修正。
     */
    fun detectType(
        content: String,
        options: List<String>,
        answer: String,
        initialType: String
    ): String {
        // FILL 填空题：有空白标记且无选项且答案为文本
        val hasBlank = content.contains("____") || content.contains("___") ||
            content.contains("（）") || content.contains("( )") ||
            content.contains("（  ）") || content.contains("_____")
        val isTextAnswer = answer.isNotEmpty() &&
            answer !in listOf("正确", "错误") &&
            !answer.all { it in 'A'..'Z' }

        if (options.isEmpty() && isTextAnswer && hasBlank) {
            return "FILL"
        }

        // JUDGE 判断题
        if (options.size == 2) {
            val optTexts = options.map { it.substringAfter(". ").trim() }
            if (optTexts.containsAll(listOf("正确", "错误")) ||
                optTexts.containsAll(listOf("对", "错"))) {
                return "JUDGE"
            }
        }
        if (answer in listOf("正确", "错误") && options.isEmpty()) {
            return "JUDGE"
        }

        // MULTI 多选题：答案为多个字母
        if (answer.length > 1 && answer.all { it in 'A'..'Z' } && answer !in listOf("正确", "错误")) {
            return "MULTI"
        }

        return initialType
    }
}
