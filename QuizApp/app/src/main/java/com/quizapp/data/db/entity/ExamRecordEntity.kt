package com.quizapp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exam_records",
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
data class ExamRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankId: Long,
    val score: Int,
    val totalCount: Int,
    val correctCount: Int,
    val questionDetails: String = "", // JSON
    val examDate: Long = System.currentTimeMillis()
)
