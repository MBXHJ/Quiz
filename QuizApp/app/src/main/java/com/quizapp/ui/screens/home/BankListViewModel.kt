package com.quizapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.SettingsManager
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val dailyGoalTarget: Int = 50,
    val todayAnswered: Int = 0,
    val todayTargetMet: Boolean = false,
    val currentStreak: Int = 0,
    val todayProgress: Float = 0f
)

@HiltViewModel
class BankListViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val banks: StateFlow<List<QuestionBankEntity>> = quizRepository.getAllBanks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    init {
        loadDailyStats()
    }

    fun loadDailyStats() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.format(Date())
            val goalTarget = settingsManager.dailyGoalTargetFlow.first()
            val stats = quizRepository.getStatsForDate(today)
            val streak = quizRepository.calculateStreak(goalTarget)

            val answered = stats?.questionsAnswered ?: 0
            val targetMet = stats?.targetMet ?: false
            val progress = if (goalTarget > 0) (answered.toFloat() / goalTarget).coerceIn(0f, 1f) else 0f

            _homeState.value = HomeUiState(
                dailyGoalTarget = goalTarget,
                todayAnswered = answered,
                todayTargetMet = targetMet,
                currentStreak = streak,
                todayProgress = progress
            )
        }
    }

    fun deleteBank(bank: QuestionBankEntity) {
        viewModelScope.launch {
            quizRepository.deleteBank(bank)
        }
    }
}
