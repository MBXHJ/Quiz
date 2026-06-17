package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quizapp.data.db.entity.PracticeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeRecordDao {

    @Query("SELECT * FROM practice_records WHERE bankId = :bankId ORDER BY endTime DESC")
    fun getRecordsByBank(bankId: Long): Flow<List<PracticeRecordEntity>>

    @Query("SELECT * FROM practice_records ORDER BY endTime DESC LIMIT :limit")
    fun getRecentRecords(limit: Int = 20): Flow<List<PracticeRecordEntity>>

    @Query("SELECT * FROM practice_records WHERE id = :id")
    suspend fun getRecordById(id: Long): PracticeRecordEntity?

    @Update
    suspend fun updateRecord(record: PracticeRecordEntity)

    @Insert
    suspend fun insertRecord(record: PracticeRecordEntity): Long

    @Query("DELETE FROM practice_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM practice_records WHERE bankId = :bankId")
    suspend fun deleteRecordsByBank(bankId: Long)
}
