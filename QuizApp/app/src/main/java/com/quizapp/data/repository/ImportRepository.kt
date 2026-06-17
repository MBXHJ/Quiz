package com.quizapp.data.repository

import android.content.Context
import android.net.Uri
import com.quizapp.data.db.dao.QuestionBankDao
import com.quizapp.data.db.dao.QuestionDao
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.parser.DocxParser
import com.quizapp.data.parser.ExcelParser
import com.quizapp.data.parser.TxtParser
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
        val failCount: Int
    )

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
            fileName.endsWith(".md", ignoreCase = true) || fileName.endsWith(".txt", ignoreCase = true) -> {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("无法读取文件")
                val text = inputStream.bufferedReader().use { it.readText() }
                TxtParser().parse(text)
            }
            else -> throw IllegalArgumentException("不支持的文件格式: $fileName")
        }

        val questions = parsedQuestions.map { parsed ->
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
            failCount = parsedQuestions.size - questions.size
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

            val questions = parsedQuestions.map { parsed ->
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
                failCount = parsedQuestions.size - questions.size
            )
        }
}
