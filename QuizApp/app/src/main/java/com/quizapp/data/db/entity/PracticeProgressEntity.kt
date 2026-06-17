package com.quizapp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "practice_progress",
    foreignKeys = [
        ForeignKey(
            entity = QuestionBankEntity::class,
            parentColumns = ["id"],
            childColumns = ["bankId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bankId", "mode", unique = true)]
)
data class PracticeProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankId: Long,
    val mode: String,
    val currentIndex: Int = 0,
    val totalQuestions: Int = 0,
    val answeredCount: Int = 0,
    val correctCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
