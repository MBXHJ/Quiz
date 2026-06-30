package com.quizapp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "review_schedule",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId", unique = true), Index("nextReviewDate")]
)
data class ReviewScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val stage: Int = 0,
    val nextReviewDate: Long = System.currentTimeMillis(),
    val totalReviews: Int = 0,
    val lastReviewDate: Long = System.currentTimeMillis()
)
