package com.quizapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.ExamRecordEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.repository.QuizRepository
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
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val quizRepository: QuizRepository
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

            _uiState.value = ProfileUiState(
                totalBanks = banks.size,
                totalQuestions = totalQuestions,
                totalAnswered = totalAnswered,
                totalWrong = totalWrong,
                totalExamCount = allExamRecords.size,
                examRecords = sortedExamRecords,
                practiceRecords = allPracticeRecords,
                bankNames = bankNamesMap,
                isLoading = false
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
}
