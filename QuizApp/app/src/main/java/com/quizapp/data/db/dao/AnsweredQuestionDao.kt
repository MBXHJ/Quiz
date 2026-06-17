package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.AnsweredQuestionEntity

@Dao
interface AnsweredQuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markAnswered(entity: AnsweredQuestionEntity)

    @Query("SELECT COUNT(*) FROM answered_questions WHERE bankId = :bankId")
    suspend fun getAnsweredCountByBank(bankId: Long): Int

    @Query("SELECT COUNT(*) FROM answered_questions WHERE bankId = :bankId AND questionId IN (SELECT id FROM questions WHERE bankId = :bankId)")
    suspend fun getAnsweredCount(bankId: Long): Int

    @Query("SELECT COUNT(*) FROM answered_questions WHERE questionId = :questionId")
    suspend fun isQuestionAnswered(questionId: Long): Int

    @Query("DELETE FROM answered_questions WHERE bankId = :bankId")
    suspend fun clearByBank(bankId: Long)

    @Query("SELECT COUNT(*) FROM answered_questions")
    suspend fun getTotalAnsweredCount(): Int
}
