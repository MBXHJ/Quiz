package com.quizapp.data.repository

import com.quizapp.data.db.dao.AnsweredQuestionDao
import com.quizapp.data.db.dao.DailyStatsDao
import com.quizapp.data.db.dao.ExamRecordDao
import com.quizapp.data.db.dao.FavoriteQuestionDao
import com.quizapp.data.db.dao.MarkedQuestionDao
import com.quizapp.data.db.dao.PracticeProgressDao
import com.quizapp.data.db.dao.PracticeRecordDao
import com.quizapp.data.db.dao.QuestionBankDao
import com.quizapp.data.db.dao.QuestionDao
import com.quizapp.data.db.dao.QuestionNoteDao
import com.quizapp.data.db.dao.QuestionTagDao
import com.quizapp.data.db.dao.ReviewScheduleDao
import com.quizapp.data.db.dao.TagDao
import com.quizapp.data.db.dao.WrongRecordDao
import com.quizapp.data.db.dao.WrongWithQuestion
import com.quizapp.data.db.entity.AnsweredQuestionEntity
import com.quizapp.data.db.entity.DailyStatsEntity
import com.quizapp.data.db.entity.ExamRecordEntity
import com.quizapp.data.db.entity.FavoriteQuestionEntity
import com.quizapp.data.db.entity.MarkedQuestionEntity
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.db.entity.QuestionNoteEntity
import com.quizapp.data.db.entity.QuestionTagEntity
import com.quizapp.data.db.entity.ReviewScheduleEntity
import com.quizapp.data.db.entity.TagEntity
import com.quizapp.data.db.entity.WrongRecordEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
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
    private val questionNoteDao: QuestionNoteDao,
    private val reviewScheduleDao: ReviewScheduleDao,
    private val dailyStatsDao: DailyStatsDao,
    private val tagDao: TagDao,
    private val questionTagDao: QuestionTagDao
) {
    companion object {
        val REVIEW_INTERVALS = listOf(0L, 1L, 2L, 4L, 7L, 15L, 30L)
    }

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

    // ===== Review Schedule (Ebbinghaus Spaced Repetition) =====

    fun getDueReviews(date: Long): Flow<List<ReviewScheduleEntity>> = reviewScheduleDao.getDueReviews(date)
    suspend fun getDueReviewsOnce(date: Long): List<ReviewScheduleEntity> = reviewScheduleDao.getDueReviewsOnce(date)
    suspend fun getDueReviewCount(date: Long): Int = reviewScheduleDao.getDueReviewCount(date)
    suspend fun getDueReviewCountByBank(bankId: Long, date: Long): Int = reviewScheduleDao.getDueReviewCountByBank(bankId, date)
    suspend fun getScheduleForQuestion(questionId: Long): ReviewScheduleEntity? = reviewScheduleDao.getScheduleForQuestion(questionId)

    suspend fun getDueReviewQuestionsByBank(bankId: Long, date: Long): List<QuestionEntity> {
        val schedules = reviewScheduleDao.getDueReviewsOnce(date)
        val questionIds = schedules.map { it.questionId }
        if (questionIds.isEmpty()) return emptyList()
        return questionDao.getQuestionsByIdsOnce(questionIds).filter { it.bankId == bankId }
    }

    suspend fun scheduleReview(questionId: Long) {
        val existing = reviewScheduleDao.getScheduleForQuestion(questionId)
        val intervalDays = REVIEW_INTERVALS[0] // immediate for initial/failed review
        if (existing != null) {
            reviewScheduleDao.upsertSchedule(
                existing.copy(
                    stage = 0,
                    nextReviewDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(intervalDays),
                    lastReviewDate = System.currentTimeMillis()
                )
            )
        } else {
            reviewScheduleDao.upsertSchedule(
                ReviewScheduleEntity(
                    questionId = questionId,
                    stage = 0,
                    nextReviewDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(intervalDays),
                    totalReviews = 0,
                    lastReviewDate = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun advanceReview(questionId: Long) {
        val existing = reviewScheduleDao.getScheduleForQuestion(questionId) ?: return
        val newStage = (existing.stage + 1).coerceAtMost(REVIEW_INTERVALS.size - 1)
        val intervalDays = REVIEW_INTERVALS[newStage]
        reviewScheduleDao.upsertSchedule(
            existing.copy(
                stage = newStage,
                nextReviewDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(intervalDays),
                totalReviews = existing.totalReviews + 1,
                lastReviewDate = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetReview(questionId: Long) {
        val existing = reviewScheduleDao.getScheduleForQuestion(questionId) ?: return
        val intervalDays = REVIEW_INTERVALS[0]
        reviewScheduleDao.upsertSchedule(
            existing.copy(
                stage = 0,
                nextReviewDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(intervalDays),
                totalReviews = existing.totalReviews + 1,
                lastReviewDate = System.currentTimeMillis()
            )
        )
    }

    // ===== Daily Stats (Daily Goal + Streak) =====

    suspend fun getStatsForDate(date: String): DailyStatsEntity? = dailyStatsDao.getStatsForDate(date)
    fun getStatsForDateFlow(date: String): Flow<DailyStatsEntity?> = dailyStatsDao.getStatsForDateFlow(date)
    suspend fun getStatsForDateRange(startDate: String, endDate: String): List<DailyStatsEntity> =
        dailyStatsDao.getStatsForDateRange(startDate, endDate)
    fun getStatsForDateRangeFlow(startDate: String, endDate: String): Flow<List<DailyStatsEntity>> =
        dailyStatsDao.getStatsForDateRangeFlow(startDate, endDate)

    suspend fun incrementDailyAnswered(date: String, duration: Long) {
        val existing = dailyStatsDao.getStatsForDate(date)
        if (existing != null) {
            dailyStatsDao.incrementDailyAnswered(date, duration)
        } else {
            dailyStatsDao.upsertDailyStat(
                DailyStatsEntity(date = date, questionsAnswered = 1, practiceDuration = duration)
            )
        }
    }

    suspend fun checkAndUpdateDailyTarget(date: String, target: Int) {
        val stats = dailyStatsDao.getStatsForDate(date)
        if (stats != null && stats.questionsAnswered >= target && !stats.targetMet) {
            dailyStatsDao.updateTargetMet(date, true)
        }
    }

    suspend fun getTotalDailyQuestionsAnswered(): Int = dailyStatsDao.getTotalQuestionsAnswered()

    suspend fun calculateStreak(target: Int): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())

        // Get all stats in one query
        val cal = java.util.Calendar.getInstance()
        cal.time = sdf.parse(today)!!
        cal.add(java.util.Calendar.DAY_OF_YEAR, -365)
        val startDate = sdf.format(cal.time)
        val allStats = dailyStatsDao.getStatsForDateRange(startDate, today)
        val statsMap = allStats.associateBy { it.date }

        var streak = 0
        var currentDate = today
        var firstDay = true

        while (true) {
            val stats = statsMap[currentDate]
            if (stats != null && stats.targetMet) {
                streak++
            } else if (firstDay) {
                // Today not yet met — check if yesterday was met (streak carries over)
                firstDay = false
                cal.time = sdf.parse(today)!!
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                currentDate = sdf.format(cal.time)
                continue
            } else {
                break
            }

            cal.time = sdf.parse(currentDate)!!
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            currentDate = sdf.format(cal.time)
            firstDay = false
        }
        return streak
    }

    // ===== Tags =====

    fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()
    suspend fun getAllTagsOnce(): List<TagEntity> = tagDao.getAllTagsOnce()
    suspend fun getTagById(id: Long): TagEntity? = tagDao.getTagById(id)
    suspend fun createTag(name: String, color: Int = 0xFF2563EB.toInt()): Long {
        return tagDao.insertTag(TagEntity(name = name, color = color))
    }
    suspend fun deleteTag(id: Long) {
        questionTagDao.removeAllForTag(id)
        tagDao.deleteTag(id)
    }

    fun getTagsForQuestion(questionId: Long): Flow<List<TagEntity>> = questionTagDao.getTagsForQuestion(questionId)
    suspend fun getTagsForQuestionOnce(questionId: Long): List<TagEntity> = questionTagDao.getTagsForQuestionOnce(questionId)
    suspend fun getTagIdsForQuestion(questionId: Long): List<Long> = questionTagDao.getTagIdsForQuestion(questionId)
    suspend fun getQuestionsForTags(tagIds: List<Long>, bankId: Long): List<QuestionEntity> =
        questionTagDao.getQuestionsForTags(tagIds, bankId)
    suspend fun addTagToQuestion(questionId: Long, tagId: Long) =
        questionTagDao.addTagToQuestion(QuestionTagEntity(questionId = questionId, tagId = tagId))
    suspend fun removeTagFromQuestion(questionId: Long, tagId: Long) =
        questionTagDao.removeTagFromQuestion(questionId, tagId)
    suspend fun getTagQuestionCounts(bankId: Long): Map<Long, Int> =
        questionTagDao.getTagQuestionCounts(bankId).associate { it.tagId to it.count }

    // ===== Analytics (Charts + Weak Areas) =====

    suspend fun getDailyAccuracy(days: Int): List<Pair<String, Float>> {
        val records = practiceRecordDao.getAllRecordsOnce()
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())
        val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())

        val grouped = records
            .filter { it.endTime >= cutoff }
            .groupBy { sdf.format(Date(it.endTime)) }

        // Fill in all days in range
        val result = mutableListOf<Pair<String, Float>>()
        val cal = java.util.Calendar.getInstance()
        for (i in (days - 1) downTo 0) {
            cal.time = Date()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
            val dateKey = sdf.format(cal.time)
            val dayRecords = grouped[dateKey] ?: emptyList()
            val total = dayRecords.sumOf { it.answeredCount }
            val correct = dayRecords.sumOf { it.correctCount }
            val accuracy = if (total > 0) correct.toFloat() / total * 100f else -1f
            result.add(dateKey to accuracy)
        }
        return result
    }

    suspend fun getDailyVolume(days: Int): List<Pair<String, Int>> {
        val records = practiceRecordDao.getAllRecordsOnce()
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())
        val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())

        val grouped = records
            .filter { it.endTime >= cutoff }
            .groupBy { sdf.format(Date(it.endTime)) }
            .mapValues { (_, recs) -> recs.sumOf { it.answeredCount } }

        val result = mutableListOf<Pair<String, Int>>()
        val cal = java.util.Calendar.getInstance()
        for (i in (days - 1) downTo 0) {
            cal.time = Date()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
            val dateKey = sdf.format(cal.time)
            result.add(dateKey to (grouped[dateKey] ?: 0))
        }
        return result
    }

    suspend fun getAccuracyByType(bankId: Long): Map<String, Pair<Float, Int>> {
        val questions = questionDao.getAllQuestionsOnce(bankId)
        val questionMap = questions.associateBy { it.id }

        // Count wrong records by question type
        val wrongRecords = wrongRecordDao.getWrongRecordsByBankOnce(bankId)
        val wrongByType = mutableMapOf<String, Int>()
        for (wr in wrongRecords) {
            val q = questionMap[wr.record.questionId] ?: continue
            val typeLabel = when (q.questionType) {
                "SINGLE" -> "单选题"
                "MULTI" -> "多选题"
                "JUDGE" -> "判断题"
                else -> q.questionType
            }
            wrongByType[typeLabel] = (wrongByType[typeLabel] ?: 0) + 1
        }

        // Count total questions by type
        val totalByType = questions.groupBy { q ->
            when (q.questionType) {
                "SINGLE" -> "单选题"
                "MULTI" -> "多选题"
                "JUDGE" -> "判断题"
                else -> q.questionType
            }
        }.mapValues { it.value.size }

        val result = mutableMapOf<String, Pair<Float, Int>>()
        for ((type, total) in totalByType) {
            val wrong = wrongByType[type] ?: 0
            // Estimate: wrong records represent questions the user got wrong
            // This is approximate since wrongCount tracks total wrongs, not unique questions
            if (total > 0) {
                // Use wrong record count as a signal (capped at total)
                val wrongRatio = (wrong.toFloat() / total).coerceAtMost(1f)
                val accuracy = (1f - wrongRatio) * 100f
                result[type] = accuracy to total
            } else {
                result[type] = -1f to 0
            }
        }
        return result
    }

    data class BankAccuracySummary(
        val bankId: Long,
        val bankName: String,
        val overallAccuracy: Float,
        val typeAccuracies: Map<String, Pair<Float, Int>>,
        val totalAnswered: Int
    )

    suspend fun getBankAccuracySummary(): List<BankAccuracySummary> {
        val banks = bankDao.getAllBanksOnce()
        return banks.map { bank ->
            val records = practiceRecordDao.getAllRecordsOnce().filter { it.bankId == bank.id }
            val totalAnswered = records.sumOf { it.answeredCount }
            val totalCorrect = records.sumOf { it.correctCount }
            val accuracy = if (totalAnswered > 0) totalCorrect.toFloat() / totalAnswered * 100f else -1f
            val typeAccuracies = getAccuracyByType(bank.id)

            BankAccuracySummary(
                bankId = bank.id,
                bankName = bank.name,
                overallAccuracy = accuracy,
                typeAccuracies = typeAccuracies,
                totalAnswered = totalAnswered
            )
        }
    }

    suspend fun getWeakBanks(threshold: Float = 60f): List<BankAccuracySummary> {
        return getBankAccuracySummary().filter { it.overallAccuracy in 0.1f..threshold }
    }
}
