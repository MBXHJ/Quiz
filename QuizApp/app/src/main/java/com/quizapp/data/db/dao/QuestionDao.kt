package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE bankId = :bankId ORDER BY id ASC")
    fun getQuestionsByBank(bankId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE bankId = :bankId AND questionType = :type ORDER BY id ASC")
    fun getQuestionsByType(bankId: Long, type: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    fun getQuestionsByIds(ids: List<Long>): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    suspend fun getQuestionsByIdsOnce(ids: List<Long>): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions WHERE bankId = :bankId")
    fun getCountByBank(bankId: Long): Flow<Int>

    @Query("SELECT * FROM questions WHERE bankId = :bankId AND questionType = :type ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsByType(bankId: Long, type: String, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE bankId = :bankId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(bankId: Long, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE bankId = :bankId ORDER BY RANDOM()")
    suspend fun getAllQuestionsRandom(bankId: Long): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE bankId = :bankId ORDER BY id ASC")
    suspend fun getAllQuestionsOnce(bankId: Long): List<QuestionEntity>
}
