package com.quizapp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "practice_records",
    foreignKeys = [
        ForeignKey(
            entity = QuestionBankEntity::class,
            parentColumns = ["id"],
            childColumns = ["bankId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bankId")]
)
data class PracticeRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankId: Long,
    val mode: String,
    val modeLabel: String = "",
    val totalCount: Int,
    val answeredCount: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val wrongQuestionIds: String = "", // JSON array of question IDs that were wrong
    val isCompleted: Boolean = false,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis()
)
