package com.quizapp.util

/**
 * 规范化答案格式，处理逗号、空格等分隔符差异。
 * "A, C" → "AC", "A, C, D" → "ACD", "A" → "A"
 * 判断题答案保持不变（"正确"/"错误"）
 */
fun normalizeAnswer(answer: String, questionType: String): String {
    if (questionType == "JUDGE") return answer.trim()
    if (questionType == "FILL") return answer.trim()
    return answer.replace(Regex("""[\s,，、]+"""), "")
        .uppercase()
        .toCharArray()
        .sorted()
        .joinToString("")
}
