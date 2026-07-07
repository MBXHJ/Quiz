package com.quizapp.data.parser

/**
 * 校验题目数据的完整性、一致性和格式正确性。
 * 在规范化之后、写入数据库之前执行。
 */
object QuestionValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )

    /**
     * 校验单道题，返回校验结果和错误列表。
     */
    fun validate(question: ParsedQuestion): ValidationResult {
        val errors = mutableListOf<String>()

        // 1. 题目内容不能为空
        if (question.content.isBlank()) {
            errors.add("题目内容为空")
        }

        // 2. 题目类型必须有效
        if (question.questionType !in listOf("SINGLE", "MULTI", "JUDGE", "FILL")) {
            errors.add("未知题目类型: ${question.questionType}")
        }

        // 3. 答案不能为空
        if (question.answer.isBlank()) {
            errors.add("答案为空")
        }

        // 4. 按类型校验
        when (question.questionType) {
            "SINGLE" -> validateSingle(question, errors)
            "MULTI" -> validateMulti(question, errors)
            "JUDGE" -> validateJudge(question, errors)
            "FILL" -> validateFill(question, errors)
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    /**
     * 批量校验，返回有效题目列表和无效题目详情。
     */
    fun validateAll(questions: List<ParsedQuestion>): ValidatedBatch {
        val valid = mutableListOf<ParsedQuestion>()
        val invalid = mutableListOf<InvalidQuestion>()

        questions.forEachIndexed { index, q ->
            val result = validate(q)
            if (result.isValid) {
                valid.add(q)
            } else {
                invalid.add(InvalidQuestion(index = index, content = q.content.take(60), errors = result.errors))
            }
        }

        return ValidatedBatch(valid, invalid)
    }

    private fun validateSingle(question: ParsedQuestion, errors: MutableList<String>) {
        if (question.options.isEmpty()) {
            errors.add("单选题缺少选项")
        }
        if (question.options.size < 2) {
            errors.add("单选题至少需要2个选项，当前${question.options.size}个")
        }
        // 单选题答案必须是单个字母
        if (question.answer.isNotEmpty() &&
            !(question.answer.length == 1 && question.answer[0] in 'A'..'Z')
        ) {
            errors.add("单选题答案应为单个字母(A-Z)，当前: ${question.answer}")
        }
        // 答案必须在选项范围内
        if (question.answer.length == 1 && question.answer[0] in 'A'..'Z') {
            val letterIndex = question.answer[0] - 'A'
            if (letterIndex >= question.options.size) {
                errors.add("答案${question.answer}超出选项范围(共${question.options.size}个选项)")
            }
        }
    }

    private fun validateMulti(question: ParsedQuestion, errors: MutableList<String>) {
        if (question.options.isEmpty()) {
            errors.add("多选题缺少选项")
        }
        if (question.options.size < 2) {
            errors.add("多选题至少需要2个选项，当前${question.options.size}个")
        }
        // 多选题答案必须是多个字母
        if (question.answer.isNotEmpty() &&
            !(question.answer.all { it in 'A'..'Z' })
        ) {
            errors.add("多选题答案应为字母组合(A-Z)，当前: ${question.answer}")
        }
        // 每个答案字母必须在选项范围内
        for (c in question.answer) {
            val idx = c - 'A'
            if (idx >= question.options.size) {
                errors.add("答案${c}超出选项范围(共${question.options.size}个选项)")
            }
        }
    }

    private fun validateJudge(question: ParsedQuestion, errors: MutableList<String>) {
        // 判断题答案必须是 "正确" 或 "错误"
        if (question.answer.isNotEmpty() && question.answer !in listOf("正确", "错误")) {
            errors.add("判断题答案应为'正确'或'错误'，当前: ${question.answer}")
        }
        // 判断题必须有 A.正确 B.错误 选项
        if (question.options.isNotEmpty() && question.options.size != 2) {
            errors.add("判断题选项数量应为2，当前${question.options.size}个")
        }
        if (question.options.size == 2) {
            val optTexts = question.options.map { it.substringAfter(". ").trim() }
            if (!optTexts.containsAll(listOf("正确", "错误"))) {
                errors.add("判断题选项应为'正确'和'错误'，当前: $optTexts")
            }
        }
    }

    private fun validateFill(question: ParsedQuestion, errors: MutableList<String>) {
        // 填空题不应有选项
        if (question.options.isNotEmpty()) {
            errors.add("填空题不应包含选项")
        }
        // 填空题答案不应是字母
        if (question.answer.isNotEmpty() && question.answer.length == 1 && question.answer[0] in 'A'..'Z') {
            errors.add("填空题答案不应为选项字母，当前: ${question.answer}")
        }
        // 填空题应有空白标记
        val hasBlank = question.content.contains("____") || question.content.contains("___") ||
            question.content.contains("（）") || question.content.contains("( )") ||
            question.content.contains("_____")
        if (!hasBlank && question.options.isEmpty()) {
            errors.add("填空题内容中缺少空白标记(____或（）)")
        }
    }

    data class InvalidQuestion(
        val index: Int,
        val content: String,
        val errors: List<String>
    )

    data class ValidatedBatch(
        val valid: List<ParsedQuestion>,
        val invalid: List<InvalidQuestion>
    )
}
