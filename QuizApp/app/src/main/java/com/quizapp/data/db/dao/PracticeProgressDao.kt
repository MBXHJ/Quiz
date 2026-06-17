package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.PracticeProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeProgressDao {

    @Query("SELECT * FROM practice_progress WHERE bankId = :bankId AND mode = :mode LIMIT 1")
    suspend fun getProgress(bankId: Long, mode: String): PracticeProgressEntity?

    @Query("SELECT * FROM practice_progress WHERE bankId = :bankId AND mode = :mode LIMIT 1")
    fun getProgressFlow(bankId: Long, mode: String): Flow<PracticeProgressEntity?>

    @Query("SELECT * FROM practice_progress WHERE bankId = :bankId")
    fun getAllProgressByBank(bankId: Long): Flow<List<PracticeProgressEntity>>

    @Query("SELECT * FROM practice_progress WHERE bankId = :bankId")
    suspend fun getAllProgressByBankOnce(bankId: Long): List<PracticeProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: PracticeProgressEntity)

    @Query("DELETE FROM practice_progress WHERE bankId = :bankId AND mode = :mode")
    suspend fun clearProgress(bankId: Long, mode: String)

    @Query("DELETE FROM practice_progress WHERE bankId = :bankId")
    suspend fun clearAllProgressByBank(bankId: Long)
}
