package com.quizapp.data.parser

interface QuestionParser {
    fun parse(content: String): List<ParsedQuestion>
}
