package com.quizapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_banks")
data class QuestionBankEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val questionCount: Int = 0,
    val importDate: Long = System.currentTimeMillis(),
    val examConfig: String = "" // JSON: {"SINGLE":70,"MULTI":20,"JUDGE":10}
)
