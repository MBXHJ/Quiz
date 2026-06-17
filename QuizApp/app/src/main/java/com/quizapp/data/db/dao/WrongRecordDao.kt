package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.db.entity.WrongRecordEntity

data class WrongWithQuestion(
    @Embedded val record: WrongRecordEntity,
    @Relation(
        parentColumn = "questionId",
        entityColumn = "id"
    )
    val question: QuestionEntity
)

@Dao
interface WrongRecordDao {

    @Query("SELECT id, questionId, wrongCount, lastWrongTime, isRemoved FROM wrong_records WHERE questionId IN (SELECT id FROM questions WHERE bankId = :bankId) AND isRemoved = 0 ORDER BY lastWrongTime DESC")
    fun getWrongRecordsByBank(bankId: Long): kotlinx.coroutines.flow.Flow<List<WrongWithQuestion>>

    @Query("SELECT id, questionId, wrongCount, lastWrongTime, isRemoved FROM wrong_records WHERE questionId IN (SELECT id FROM questions WHERE bankId = :bankId) AND isRemoved = 0 ORDER BY lastWrongTime DESC")
    suspend fun getWrongRecordsByBankOnce(bankId: Long): List<WrongWithQuestion>

    @Query("SELECT wrongCount FROM wrong_records WHERE questionId = :questionId")
    suspend fun getWrongCount(questionId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWrongRecord(record: WrongRecordEntity)

    @Query("UPDATE wrong_records SET isRemoved = 1 WHERE questionId = :questionId")
    suspend fun removeWrongRecord(questionId: Long)

    @Query("DELETE FROM wrong_records WHERE questionId IN (SELECT id FROM questions WHERE bankId = :bankId)")
    suspend fun clearWrongRecordsByBank(bankId: Long)
}
