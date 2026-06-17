package com.quizapp.data.parser

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class JsonParser : QuestionParser {

    private val json = Json { ignoreUnknownKeys = true }

    override fun parse(content: String): List<ParsedQuestion> {
        val element = json.parseToJsonElement(content)
        val items = if (element is JsonArray) element else json.parseToJsonElement("[$content]").jsonArray

        return items.mapNotNull { item ->
            try {
                val obj = item.jsonObject
                val rawType = obj["type"]?.jsonPrimitive?.content ?: "单选题"
                val rawOptions = obj["options"]

                val questionType = when (rawType) {
                    "单选题" -> "SINGLE"
                    "多选题" -> "MULTI"
                    "判断题" -> "JUDGE"
                    else -> "SINGLE"
                }

                val options = mutableListOf<String>()
                if (rawOptions is JsonObject) {
                    for (key in listOf("A", "B", "C", "D", "E", "F")) {
                        val value = rawOptions[key]?.jsonPrimitive?.content
                        if (value != null) {
                            options.add("$key. $value")
                        }
                    }
                }

                val analysis = obj["analysis"]?.jsonPrimitive?.content ?: ""

                ParsedQuestion(
                    content = obj["question"]?.jsonPrimitive?.content ?: "",
                    questionType = questionType,
                    options = options,
                    answer = obj["answer"]?.jsonPrimitive?.content ?: "",
                    analysis = analysis
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
