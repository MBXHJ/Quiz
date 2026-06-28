package com.quizapp.data.repository

import com.quizapp.data.db.dao.AnsweredQuestionDao
import com.quizapp.data.db.dao.ExamRecordDao
import com.quizapp.data.db.dao.FavoriteQuestionDao
import com.quizapp.data.db.dao.MarkedQuestionDao
import com.quizapp.data.db.dao.PracticeProgressDao
import com.quizapp.data.db.dao.PracticeRecordDao
import com.quizapp.data.db.dao.QuestionBankDao
import com.quizapp.data.db.dao.QuestionDao
import com.quizapp.data.db.dao.QuestionNoteDao
import com.quizapp.data.db.dao.WrongRecordDao
import com.quizapp.data.db.dao.WrongWithQuestion
import com.quizapp.data.db.entity.AnsweredQuestionEntity
import com.quizapp.data.db.entity.ExamRecordEntity
import com.quizapp.data.db.entity.FavoriteQuestionEntity
import com.quizapp.data.db.entity.MarkedQuestionEntity
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.db.entity.QuestionNoteEntity
import com.quizapp.data.db.entity.WrongRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val bankDao: QuestionBankDao,
    private val questionDao: QuestionDao,
    private val wrongRecordDao: WrongRecordDao,
    private val examRecordDao: ExamRecordDao,
    private val answeredQuestionDao: AnsweredQuestionDao,
    private val practiceProgressDao: PracticeProgressDao,
    private val practiceRecordDao: PracticeRecordDao,
    private val favoriteQuestionDao: FavoriteQuestionDao,
    private val markedQuestionDao: MarkedQuestionDao,
    private val questionNoteDao: QuestionNoteDao
) {
    // Question Banks
    fun getAllBanks(): Flow<List<QuestionBankEntity>> = bankDao.getAllBanks()
    fun getBankById(id: Long): Flow<QuestionBankEntity?> = bankDao.getBankById(id)
    suspend fun getBankByIdOnce(id: Long): QuestionBankEntity? = bankDao.getBankByIdOnce(id)
    suspend fun insertBank(bank: QuestionBankEntity): Long = bankDao.insertBank(bank)
    suspend fun deleteBank(bank: QuestionBankEntity) = bankDao.deleteBank(bank)
    suspend fun deleteBankById(id: Long) = bankDao.deleteBankById(id)
    suspend fun updateQuestionCount(bankId: Long, count: Int) = bankDao.updateQuestionCount(bankId, count)
    suspend fun getBankCount(): Int = bankDao.getBankCount()

    // Questions
    fun getQuestionsByBank(bankId: Long): Flow<List<QuestionEntity>> = questionDao.getQuestionsByBank(bankId)
    fun getQuestionsByType(bankId: Long, type: String): Flow<List<QuestionEntity>> = questionDao.getQuestionsByType(bankId, type)
    fun getQuestionsByIds(ids: List<Long>): Flow<List<QuestionEntity>> = questionDao.getQuestionsByIds(ids)
    suspend fun getQuestionsByIdsOnce(ids: List<Long>): List<QuestionEntity> = questionDao.getQuestionsByIdsOnce(ids)
    suspend fun getQuestionById(id: Long): QuestionEntity? = questionDao.getQuestionById(id)
    suspend fun insertQuestions(questions: List<QuestionEntity>) = questionDao.insertQuestions(questions)
    fun getCountByBank(bankId: Long): Flow<Int> = questionDao.getCountByBank(bankId)
    suspend fun getRandomQuestions(bankId: Long, limit: Int): List<QuestionEntity> = questionDao.getRandomQuestions(bankId, limit)
    suspend fun getRandomQuestionsByType(bankId: Long, type: String, limit: Int): List<QuestionEntity> = questionDao.getRandomQuestionsByType(bankId, type, limit)
    suspend fun getAllQuestionsRandom(bankId: Long): List<QuestionEntity> = questionDao.getAllQuestionsRandom(bankId)
    suspend fun getAllQuestionsOnce(bankId: Long): List<QuestionEntity> = questionDao.getAllQuestionsOnce(bankId)

    // Wrong Records
    fun getWrongRecordsByBank(bankId: Long): Flow<List<WrongWithQuestion>> = wrongRecordDao.getWrongRecordsByBank(bankId)
    suspend fun getWrongRecordsByBankOnce(bankId: Long): List<WrongWithQuestion> = wrongRecordDao.getWrongRecordsByBankOnce(bankId)
    suspend fun getWrongCount(questionId: Long): Int = wrongRecordDao.getWrongCount(questionId) ?: 0
    suspend fun upsertWrongRecord(record: WrongRecordEntity) = wrongRecordDao.upsertWrongRecord(record)
    suspend fun removeWrongRecord(questionId: Long) = wrongRecordDao.removeWrongRecord(questionId)
    suspend fun clearWrongRecordsByBank(bankId: Long) = wrongRecordDao.clearWrongRecordsByBank(bankId)

    // Exam Records
    fun getRecordsByBank(bankId: Long): Flow<List<ExamRecordEntity>> = examRecordDao.getRecordsByBank(bankId)
    suspend fun insertRecord(record: ExamRecordEntity): Long = examRecordDao.insertRecord(record)
    suspend fun deleteExamRecordById(id: Long) = examRecordDao.deleteRecordById(id)
    suspend fun getExamRecordById(id: Long): ExamRecordEntity? = examRecordDao.getRecordById(id)

    // Answered Questions
    suspend fun markQuestionAnswered(questionId: Long, bankId: Long) =
        answeredQuestionDao.markAnswered(AnsweredQuestionEntity(questionId = questionId, bankId = bankId))

    suspend fun getAnsweredCountByBank(bankId: Long): Int =
        answeredQuestionDao.getAnsweredCountByBank(bankId)

    suspend fun isQuestionAnswered(questionId: Long): Boolean =
        answeredQuestionDao.isQuestionAnswered(questionId) > 0

    suspend fun getTotalAnsweredCount(): Int =
        answeredQuestionDao.getTotalAnsweredCount()

    suspend fun clearAnsweredByBank(bankId: Long) =
        answeredQuestionDao.clearByBank(bankId)

    // Practice Progress
    suspend fun getPracticeProgress(bankId: Long, mode: String): PracticeProgressEntity? =
        practiceProgressDao.getProgress(bankId, mode)

    fun getPracticeProgressFlow(bankId: Long, mode: String): Flow<PracticeProgressEntity?> =
        practiceProgressDao.getProgressFlow(bankId, mode)

    fun getAllProgressByBank(bankId: Long): Flow<List<PracticeProgressEntity>> =
        practiceProgressDao.getAllProgressByBank(bankId)

    suspend fun savePracticeProgress(progress: PracticeProgressEntity) =
        practiceProgressDao.saveProgress(progress)

    suspend fun clearPracticeProgress(bankId: Long, mode: String) =
        practiceProgressDao.clearProgress(bankId, mode)

    suspend fun clearAllPracticeProgressByBank(bankId: Long) =
        practiceProgressDao.clearAllProgressByBank(bankId)

    // Practice Records
    fun getPracticeRecordsByBank(bankId: Long): Flow<List<PracticeRecordEntity>> =
        practiceRecordDao.getRecordsByBank(bankId)

    fun getRecentPracticeRecords(limit: Int = 20): Flow<List<PracticeRecordEntity>> =
        practiceRecordDao.getRecentRecords(limit)

    suspend fun insertPracticeRecord(record: PracticeRecordEntity): Long =
        practiceRecordDao.insertRecord(record)

    suspend fun getAllPracticeRecordsOnce(): List<PracticeRecordEntity> =
        practiceRecordDao.getAllRecordsOnce()

    suspend fun updatePracticeRecord(record: PracticeRecordEntity) =
        practiceRecordDao.updateRecord(record)

    suspend fun getPracticeRecordById(id: Long): PracticeRecordEntity? =
        practiceRecordDao.getRecordById(id)

    suspend fun deletePracticeRecordById(id: Long) =
        practiceRecordDao.deleteRecordById(id)

    // Favorites
    suspend fun getAllFavoriteIds(): List<Long> = favoriteQuestionDao.getAllFavoriteIds()
    suspend fun isFavorite(questionId: Long): Boolean = favoriteQuestionDao.isFavorite(questionId) != null
    suspend fun addFavorite(questionId: Long) = favoriteQuestionDao.addFavorite(FavoriteQuestionEntity(questionId = questionId))
    suspend fun removeFavorite(questionId: Long) = favoriteQuestionDao.removeFavorite(questionId)
    suspend fun getFavoriteCount(): Int = favoriteQuestionDao.getFavoriteCount()

    // Marked
    suspend fun getAllMarkedIds(): List<Long> = markedQuestionDao.getAllMarkedIds()
    suspend fun isMarked(questionId: Long): Boolean = markedQuestionDao.isMarked(questionId) != null
    suspend fun addMark(questionId: Long) = markedQuestionDao.addMark(MarkedQuestionEntity(questionId = questionId))
    suspend fun removeMark(questionId: Long) = markedQuestionDao.removeMark(questionId)
    suspend fun getMarkedCount(): Int = markedQuestionDao.getMarkedCount()

    // Notes
    suspend fun getNote(questionId: Long): QuestionNoteEntity? = questionNoteDao.getNote(questionId)
    suspend fun saveNote(questionId: Long, note: String) =
        questionNoteDao.saveNote(QuestionNoteEntity(questionId = questionId, note = note, updatedTime = System.currentTimeMillis()))
    suspend fun deleteNote(questionId: Long) = questionNoteDao.deleteNote(questionId)
}
