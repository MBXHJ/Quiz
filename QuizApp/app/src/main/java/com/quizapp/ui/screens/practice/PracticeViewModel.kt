package com.quizapp.ui.screens.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PracticeUiState(
    val bank: QuestionBankEntity? = null,
    val questionCount: Int = 0,
    val wrongQuestionCount: Int = 0,
    val answeredQuestionCount: Int = 0,
    val sequentialProgress: PracticeProgressEntity? = null,
    val typeProgress: List<PracticeProgressEntity> = emptyList(),
    val dueReviewCount: Int = 0,
    val favoriteCount: Int = 0,
    val markedCount: Int = 0
)

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    fun loadBank(bankId: Long) {
        viewModelScope.launch {
            val bank = quizRepository.getBankByIdOnce(bankId)
            val questionCountFlow = quizRepository.getCountByBank(bankId)
            val questionCount = questionCountFlow.first()
            val wrongCount = quizRepository.getWrongRecordsByBankOnce(bankId).size
            val answeredCount = quizRepository.getAnsweredCountByBank(bankId)

            // Load saved progress
            val allProgress = quizRepository.getAllProgressByBank(bankId).first()
            val seqProgress = allProgress.find { it.mode == "sequential" }
            val typeProg = allProgress.filter { it.mode.startsWith("type_") }

            // Load due review count for this bank
            val dueReviewCount = quizRepository.getDueReviewCountByBank(bankId, System.currentTimeMillis())

            // Load favorite and marked counts
            val favCount = quizRepository.getAllFavoriteIds().count { fid ->
                quizRepository.getQuestionById(fid)?.bankId == bankId
            }
            val mkdCount = quizRepository.getAllMarkedIds().count { mid ->
                quizRepository.getQuestionById(mid)?.bankId == bankId
            }

            _uiState.value = PracticeUiState(
                bank = bank,
                questionCount = questionCount,
                wrongQuestionCount = wrongCount,
                answeredQuestionCount = answeredCount,
                sequentialProgress = seqProgress,
                typeProgress = typeProg,
                dueReviewCount = dueReviewCount,
                favoriteCount = favCount,
                markedCount = mkdCount
            )
        }
    }

}
