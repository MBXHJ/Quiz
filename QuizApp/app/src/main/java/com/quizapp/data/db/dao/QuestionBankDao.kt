package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quizapp.data.db.entity.QuestionBankEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionBankDao {

    @Query("SELECT * FROM question_banks ORDER BY importDate DESC")
    fun getAllBanks(): Flow<List<QuestionBankEntity>>

    @Query("SELECT * FROM question_banks WHERE id = :id")
    fun getBankById(id: Long): Flow<QuestionBankEntity?>

    @Query("SELECT * FROM question_banks WHERE id = :id")
    suspend fun getBankByIdOnce(id: Long): QuestionBankEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBank(bank: QuestionBankEntity): Long

    @Update
    suspend fun updateBank(bank: QuestionBankEntity)

    @Delete
    suspend fun deleteBank(bank: QuestionBankEntity)

    @Query("DELETE FROM question_banks WHERE id = :id")
    suspend fun deleteBankById(id: Long)

    @Query("UPDATE question_banks SET questionCount = :count WHERE id = :bankId")
    suspend fun updateQuestionCount(bankId: Long, count: Int)

    @Query("SELECT * FROM question_banks ORDER BY importDate DESC")
    suspend fun getAllBanksOnce(): List<QuestionBankEntity>

    @Query("SELECT COUNT(*) FROM question_banks")
    suspend fun getBankCount(): Int
}
