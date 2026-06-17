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
                        detailLine.startsWith("答案", ignoreCase = true) -> {
                            answer = detailLine.replace(Regex("""答案[:：]\s*"""), "").trim()
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
                if (options.isEmpty()) {
                    questionType = "JUDGE"
                }
                if (answer.length > 1 && !answer.contains("正确") && !answer.contains("错误")) {
                    questionType = "MULTI"
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
