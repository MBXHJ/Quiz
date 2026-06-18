package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.MarkedQuestionEntity

@Dao
interface MarkedQuestionDao {

    @Query("SELECT questionId FROM marked_questions ORDER BY createdTime DESC")
    suspend fun getAllMarkedIds(): List<Long>

    @Query("SELECT questionId FROM marked_questions WHERE questionId = :questionId LIMIT 1")
    suspend fun isMarked(questionId: Long): Long?

    @Query("SELECT COUNT(*) FROM marked_questions")
    suspend fun getMarkedCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMark(entity: MarkedQuestionEntity)

    @Query("DELETE FROM marked_questions WHERE questionId = :questionId")
    suspend fun removeMark(questionId: Long)
}
