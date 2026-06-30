package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.ReviewScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewScheduleDao {

    @Query("SELECT * FROM review_schedule WHERE nextReviewDate <= :date ORDER BY nextReviewDate ASC")
    fun getDueReviews(date: Long): Flow<List<ReviewScheduleEntity>>

    @Query("SELECT * FROM review_schedule WHERE nextReviewDate <= :date ORDER BY nextReviewDate ASC")
    suspend fun getDueReviewsOnce(date: Long): List<ReviewScheduleEntity>

    @Query("SELECT * FROM review_schedule WHERE questionId = :questionId")
    suspend fun getScheduleForQuestion(questionId: Long): ReviewScheduleEntity?

    @Query("SELECT * FROM review_schedule WHERE questionId = :questionId")
    fun getScheduleForQuestionFlow(questionId: Long): Flow<ReviewScheduleEntity?>

    @Query("SELECT COUNT(*) FROM review_schedule WHERE nextReviewDate <= :date")
    suspend fun getDueReviewCount(date: Long): Int

    @Query("""
        SELECT COUNT(*) FROM review_schedule
        WHERE nextReviewDate <= :date
        AND questionId IN (SELECT id FROM questions WHERE bankId = :bankId)
    """)
    suspend fun getDueReviewCountByBank(bankId: Long, date: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchedule(schedule: ReviewScheduleEntity)

    @Query("DELETE FROM review_schedule WHERE questionId = :questionId")
    suspend fun deleteSchedule(questionId: Long)

    @Query("DELETE FROM review_schedule WHERE questionId IN (SELECT id FROM questions WHERE bankId = :bankId)")
    suspend fun clearByBank(bankId: Long)
}
