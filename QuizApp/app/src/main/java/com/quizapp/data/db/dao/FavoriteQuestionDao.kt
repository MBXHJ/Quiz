package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.FavoriteQuestionEntity

@Dao
interface FavoriteQuestionDao {

    @Query("SELECT questionId FROM favorite_questions ORDER BY createdTime DESC")
    suspend fun getAllFavoriteIds(): List<Long>

    @Query("SELECT questionId FROM favorite_questions WHERE questionId = :questionId LIMIT 1")
    suspend fun isFavorite(questionId: Long): Long?

    @Query("SELECT COUNT(*) FROM favorite_questions")
    suspend fun getFavoriteCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(entity: FavoriteQuestionEntity)

    @Query("DELETE FROM favorite_questions WHERE questionId = :questionId")
    suspend fun removeFavorite(questionId: Long)
}
