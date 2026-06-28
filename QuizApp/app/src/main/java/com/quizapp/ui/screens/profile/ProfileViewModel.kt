package com.quizapp.ui.screens.profile

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.ExamRecordEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.repository.QuizRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val totalBanks: Int = 0,
    val totalQuestions: Int = 0,
    val totalAnswered: Int = 0,
    val totalWrong: Int = 0,
    val totalExamCount: Int = 0,
    val examRecords: List<ExamRecordEntity> = emptyList(),
    val practiceRecords: List<PracticeRecordEntity> = emptyList(),
    val bankNames: Map<Long, String> = emptyMap(),
    val isLoading: Boolean = true,
    // Learning report
    val totalPracticeTime: Long = 0L,    // in milliseconds
    val totalPracticeAnswered: Int = 0,
    val avgPracticeAccuracy: Int = 0,    // percentage
    val bestPracticeAccuracy: Int = 0    // percentage
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val banks = quizRepository.getAllBanks().first()
            val bankNamesMap = banks.associate { it.id to it.name }

            var totalQuestions = 0
            var totalWrong = 0
            var totalAnswered = 0

            // Collect stats per bank
            for (bank in banks) {
                totalQuestions += bank.questionCount
                totalWrong += quizRepository.getWrongRecordsByBankOnce(bank.id).size
                totalAnswered += quizRepository.getAnsweredCountByBank(bank.id)
            }

            // Get recent exam records (across all banks, limited to 10)
            val allExamRecords = mutableListOf<ExamRecordEntity>()
            for (bank in banks) {
                val records = quizRepository.getRecordsByBank(bank.id).first()
                allExamRecords.addAll(records)
            }
            val sortedExamRecords = allExamRecords.sortedByDescending { it.examDate }.take(10)

            // Get recent practice records
            val allPracticeRecords = quizRepository.getRecentPracticeRecords(20).first()

            // ── Learning report ──
            val allPracticeRecordsFull = quizRepository.getAllPracticeRecordsOnce()
            var totalPracticeTime = 0L
            var totalPracticeAnswered = 0
            var totalPracticeCorrect = 0
            var bestAccuracy = 0
            for (rec in allPracticeRecordsFull) {
                if (rec.endTime > rec.startTime) {
                    totalPracticeTime += (rec.endTime - rec.startTime)
                }
                totalPracticeAnswered += rec.answeredCount
                totalPracticeCorrect += rec.correctCount
                val acc = if (rec.answeredCount > 0) (rec.correctCount * 100) / rec.answeredCount else 0
                if (acc > bestAccuracy) bestAccuracy = acc
            }
            val avgAccuracy = if (totalPracticeAnswered > 0) (totalPracticeCorrect * 100) / totalPracticeAnswered else 0

            _uiState.value = ProfileUiState(
                totalBanks = banks.size,
                totalQuestions = totalQuestions,
                totalAnswered = totalAnswered,
                totalWrong = totalWrong,
                totalExamCount = allExamRecords.size,
                examRecords = sortedExamRecords,
                practiceRecords = allPracticeRecords,
                bankNames = bankNamesMap,
                isLoading = false,
                totalPracticeTime = totalPracticeTime,
                totalPracticeAnswered = totalPracticeAnswered,
                avgPracticeAccuracy = avgAccuracy,
                bestPracticeAccuracy = bestAccuracy
            )
        }
    }

    fun deletePracticeRecord(id: Long) {
        viewModelScope.launch {
            quizRepository.deletePracticeRecordById(id)
            loadStats() // reload
        }
    }

    fun deleteExamRecord(id: Long) {
        viewModelScope.launch {
            quizRepository.deleteExamRecordById(id)
            loadStats() // reload
        }
    }

    fun exportWrongQuestions() {
        viewModelScope.launch {
            try {
                val banks = quizRepository.getAllBanks().first()
                val lines = mutableListOf<String>()
                lines.add("刷题助手 - 错题集")
                lines.add("导出时间：${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
                lines.add("")
                for (bank in banks) {
                    val wrongRecords = quizRepository.getWrongRecordsByBankOnce(bank.id)
                    if (wrongRecords.isEmpty()) continue
                    val ids = wrongRecords.map { it.record.questionId }
                    val questions = quizRepository.getQuestionsByIdsOnce(ids)
                    lines.add("题库：${bank.name}")
                    lines.add("")
                    questions.forEach { q ->
                        val typeLabel = when (q.questionType) { "SINGLE" -> "单选题"; "MULTI" -> "多选题"; "JUDGE" -> "判断题"; else -> q.questionType }
                        lines.add("类型：$typeLabel")
                        lines.add("题目：${q.content}")
                        q.options.split("|||").filter { it.isNotBlank() }.forEach { lines.add("  $it") }
                        lines.add("答案：${q.answer}")
                        if (q.analysis.isNotBlank()) lines.add("解析：${q.analysis}")
                        lines.add("")
                    }
                }
                if (lines.size <= 3) { lines.add("暂无错题记录！"); lines.add("") }
                val text = lines.joinToString("\n")
                val sdf = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
                val file = java.io.File(context.cacheDir, "错题集_${sdf.format(java.util.Date())}.txt")
                file.writeText(text)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = Intent.createChooser(shareIntent, "导出错题")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
