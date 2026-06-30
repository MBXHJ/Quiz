package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.DailyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getStatsForDate(date: String): DailyStatsEntity?

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    fun getStatsForDateFlow(date: String): Flow<DailyStatsEntity?>

    @Query("SELECT * FROM daily_stats WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getStatsForDateRange(startDate: String, endDate: String): List<DailyStatsEntity>

    @Query("SELECT * FROM daily_stats WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getStatsForDateRangeFlow(startDate: String, endDate: String): Flow<List<DailyStatsEntity>>

    @Query("SELECT SUM(questionsAnswered) FROM daily_stats")
    suspend fun getTotalQuestionsAnswered(): Int

    @Query("SELECT SUM(practiceDuration) FROM daily_stats")
    suspend fun getTotalPracticeDuration(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyStat(stat: DailyStatsEntity)

    @Query("UPDATE daily_stats SET questionsAnswered = questionsAnswered + 1, practiceDuration = practiceDuration + :duration WHERE date = :date")
    suspend fun incrementDailyAnswered(date: String, duration: Long)

    @Query("UPDATE daily_stats SET targetMet = :met WHERE date = :date")
    suspend fun updateTargetMet(date: String, met: Boolean)
}
