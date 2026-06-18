package com.quizapp.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "question_notes",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId", unique = true)]
)
data class QuestionNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val note: String = "",
    val createdTime: Long = System.currentTimeMillis(),
    val updatedTime: Long = System.currentTimeMillis()
)
