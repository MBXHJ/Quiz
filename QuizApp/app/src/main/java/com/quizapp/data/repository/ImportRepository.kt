package com.quizapp.data.repository

import android.content.Context
import android.net.Uri
import com.quizapp.data.db.dao.QuestionBankDao
import com.quizapp.data.db.dao.QuestionDao
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.parser.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bankDao: QuestionBankDao,
    private val questionDao: QuestionDao
) {
    data class ImportResult(
        val bankId: Long,
        val successCount: Int,
        val failCount: Int,
        val validationErrors: List<QuestionValidator.InvalidQuestion> = emptyList()
    )

    /**
     * 统一的导入处理管道：解析 → 规范化 → 校验 → 写入DB。
     */
    suspend fun importFromUri(
        bankName: String,
        uri: Uri,
        fileName: String,
        examConfig: String = ""
    ): ImportResult = withContext(Dispatchers.IO) {
        val bank = QuestionBankEntity(name = bankName, examConfig = examConfig)
        val bankId = bankDao.insertBank(bank)

        val parsedQuestions = when {
            fileName.endsWith(".docx", ignoreCase = true) -> {
                DocxParser(context).parseFromUri(uri)
            }
            fileName.endsWith(".xlsx", ignoreCase = true) || fileName.endsWith(".xls", ignoreCase = true) -> {
                ExcelParser(context).parseFromUri(uri)
            }
            fileName.endsWith(".json", ignoreCase = true) -> {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("无法读取文件")
                val text = inputStream.bufferedReader().use { it.readText() }
                JsonParser().parse(text)
            }
            fileName.endsWith(".md", ignoreCase = true) || fileName.endsWith(".txt", ignoreCase = true) -> {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("无法读取文件")
                val text = inputStream.bufferedReader().use { it.readText() }
                TxtParser().parse(text)
            }
            else -> throw IllegalArgumentException("不支持的文件格式: $fileName")
        }

        // ── 规范化：统一格式 ──
        val normalized = parsedQuestions.map { QuestionNormalizer.normalize(it) }

        // ── 校验：过滤无效题 ──
        val batch = QuestionValidator.validateAll(normalized)
        val validQuestions = batch.valid
        val invalidQuestions = batch.invalid

        // ── 写入数据库 ──
        val questions = validQuestions.map { parsed ->
            QuestionEntity(
                bankId = bankId,
                questionType = parsed.questionType,
                content = parsed.content,
                options = parsed.options.joinToString("|||"),
                answer = parsed.answer,
                analysis = parsed.analysis
            )
        }

        questionDao.insertQuestions(questions)
        bankDao.updateQuestionCount(bankId, questions.size)

        ImportResult(
            bankId = bankId,
            successCount = questions.size,
            failCount = parsedQuestions.size - questions.size,
            validationErrors = invalidQuestions
        )
    }

    suspend fun isDatabaseEmpty(): Boolean {
        return bankDao.getBankCount() == 0
    }

    suspend fun importFromAssets(assetFileName: String, bankName: String): ImportResult =
        withContext(Dispatchers.IO) {
            val bank = QuestionBankEntity(name = bankName)
            val bankId = bankDao.insertBank(bank)

            val text = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
            val parsedQuestions = TxtParser().parse(text)

            val normalized = parsedQuestions.map { QuestionNormalizer.normalize(it) }
            val batch = QuestionValidator.validateAll(normalized)

            val questions = batch.valid.map { parsed ->
                QuestionEntity(
                    bankId = bankId,
                    questionType = parsed.questionType,
                    content = parsed.content,
                    options = parsed.options.joinToString("|||"),
                    answer = parsed.answer,
                    analysis = parsed.analysis
                )
            }

            questionDao.insertQuestions(questions)
            bankDao.updateQuestionCount(bankId, questions.size)

            ImportResult(
                bankId = bankId,
                successCount = questions.size,
                failCount = parsedQuestions.size - questions.size,
                validationErrors = batch.invalid
            )
        }

    suspend fun importDocxFromAssets(assetFileName: String, bankName: String): ImportResult =
        withContext(Dispatchers.IO) {
            val bank = QuestionBankEntity(name = bankName)
            val bankId = bankDao.insertBank(bank)

            val parsedQuestions = DocxParser(context).parseFromStream(
                context.assets.open(assetFileName)
            )

            val normalized = parsedQuestions.map { QuestionNormalizer.normalize(it) }
            val batch = QuestionValidator.validateAll(normalized)

            val questions = batch.valid.map { parsed ->
                QuestionEntity(
                    bankId = bankId,
                    questionType = parsed.questionType,
                    content = parsed.content,
                    options = parsed.options.joinToString("|||"),
                    answer = parsed.answer,
                    analysis = parsed.analysis
                )
            }

            questionDao.insertQuestions(questions)
            bankDao.updateQuestionCount(bankId, questions.size)

            ImportResult(
                bankId = bankId,
                successCount = questions.size,
                failCount = parsedQuestions.size - questions.size,
                validationErrors = batch.invalid
            )
        }
}
