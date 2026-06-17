package com.quizapp.data.parser

data class ParsedQuestion(
    val content: String,
    val questionType: String, // "SINGLE", "MULTI", "JUDGE"
    val options: List<String>,
    val answer: String,
    val analysis: String
)
