package com.quizapp.data.parser

import android.content.Context
import android.net.Uri

class DocxParser(private val context: Context) : QuestionParser {

    override fun parse(content: String): List<ParsedQuestion> {
        return emptyList()
    }

    fun parseFromUri(uri: Uri): List<ParsedQuestion> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return emptyList()

        return inputStream.use { stream ->
            val text = stream.bufferedReader().readText()
            TxtParser().parse(text)
        }
    }
}
