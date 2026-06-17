package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.quizapp.data.db.entity.ExamRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamRecordDao {

    @Query("SELECT * FROM exam_records WHERE bankId = :bankId ORDER BY examDate DESC")
    fun getRecordsByBank(bankId: Long): Flow<List<ExamRecordEntity>>

    @Insert
    suspend fun insertRecord(record: ExamRecordEntity): Long

    @Query("DELETE FROM exam_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM exam_records WHERE bankId = :bankId")
    suspend fun deleteRecordsByBank(bankId: Long)
}
