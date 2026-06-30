package com.quizapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_stats",
    indices = [Index("date", unique = true)]
)
data class DailyStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val questionsAnswered: Int = 0,
    val targetMet: Boolean = false,
    val practiceDuration: Long = 0
)
