package com.quizapp.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.JsonPrimitive

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return try {
            val el = json.parseToJsonElement(value)
            el.jsonArray.map { it.jsonPrimitive.content }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return buildJsonArray {
            list.forEach { add(JsonPrimitive(it)) }
        }.toString()
    }
}
