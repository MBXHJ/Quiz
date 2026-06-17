package com.quizapp.ui.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.db.entity.WrongRecordEntity
import com.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionUiState(
    val questions: List<QuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String = "",
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val wrongCount: Int = 0,
    val isLoading: Boolean = true,
    val isMultiSelected: Set<String> = emptySet(),
    val bankId: Long = 0,
    val mode: String = "",
    val answeredSet: Set<Int> = emptySet(),
    val correctCount: Int = 0,
    val answeredCount: Int = 0,
    val showJumpDialog: Boolean = false,
    val isFinishing: Boolean = false,
    val recordId: Long = -1L // non-null when resuming from a practice record
)

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionUiState())
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()

    fun loadQuestions(bankId: Long, mode: String, restart: Boolean = false, recordId: Long = -1L, count: Int = 0) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, mode = mode, recordId = recordId)

            // Clear saved progress if restarting
            if (restart) {
                quizRepository.clearPracticeProgress(bankId, mode)
                quizRepository.clearAnsweredByBank(bankId)
            }

            val questions = when {
                mode == "sequential" -> quizRepository.getAllQuestionsOnce(bankId)
                mode == "random" -> {
                    val all = quizRepository.getAllQuestionsRandom(bankId)
                    if (count > 0 && count < all.size) all.take(count) else all
                }
                mode == "wrong" -> {
                    val wrongRecords = quizRepository.getWrongRecordsByBankOnce(bankId)
                    quizRepository.getQuestionsByIdsOnce(wrongRecords.map { it.record.questionId })
                }
                mode.startsWith("type_") -> {
                    val type = mode.removePrefix("type_")
                    val allQuestions = quizRepository.getAllQuestionsOnce(bankId)
                    allQuestions.filter { it.questionType == type }
                }
                else -> quizRepository.getAllQuestionsOnce(bankId)
            }

            // Determine starting index from saved progress or record
            val savedProgress = if (restart) null else quizRepository.getPracticeProgress(bankId, mode)
            val record = if (savedProgress == null && recordId > 0) quizRepository.getPracticeRecordById(recordId) else null

            val startIndex = when {
                savedProgress != null && savedProgress.currentIndex in questions.indices -> savedProgress.currentIndex
                record != null && record.answeredCount in questions.indices -> record.answeredCount
                else -> 0
            }

            val restoredAnswered = when {
                savedProgress != null -> savedProgress.answeredCount
                record != null -> record.answeredCount
                else -> 0
            }

            val restoredCorrect = when {
                savedProgress != null -> savedProgress.correctCount
                record != null -> record.correctCount
                else -> 0
            }

            _uiState.value = QuestionUiState(
                questions = questions,
                currentIndex = startIndex,
                isLoading = false,
                bankId = bankId,
                mode = mode,
                recordId = recordId,
                answeredCount = restoredAnswered,
                correctCount = restoredCorrect,
                answeredSet = emptySet()
            )
        }
    }

    fun selectAnswer(answer: String) {
        val state = _uiState.value
        val question = state.questions.getOrNull(state.currentIndex) ?: return

        if (state.showResult) return // Already answered

        if (question.questionType == "MULTI") {
            val newSelected = if (answer in state.isMultiSelected) {
                state.isMultiSelected - answer
            } else {
                state.isMultiSelected + answer
            }
            _uiState.value = state.copy(isMultiSelected = newSelected)
        } else {
            val isCorrect = answer == question.answer
            val newAnswered = state.answeredSet + state.currentIndex
            val newCorrectCount = state.correctCount + if (isCorrect) 1 else 0
            val newAnsweredCount = state.answeredCount + 1

            _uiState.value = state.copy(
                selectedAnswer = answer,
                showResult = true,
                isCorrect = isCorrect,
                answeredSet = newAnswered,
                correctCount = newCorrectCount,
                answeredCount = newAnsweredCount
            )
            recordWrongIfNeeded(question.id, isCorrect)
            markAnswered(question.id, state.bankId)
            saveProgress(state.currentIndex, state.questions.size, newAnsweredCount, newCorrectCount)
        }
    }

    fun confirmMultiSelect() {
        val state = _uiState.value
        val question = state.questions.getOrNull(state.currentIndex) ?: return
        val answer = state.isMultiSelected.sorted().joinToString("")

        val isCorrect = answer == question.answer
        val newAnswered = state.answeredSet + state.currentIndex
        val newCorrectCount = state.correctCount + if (isCorrect) 1 else 0
        val newAnsweredCount = state.answeredCount + 1

        _uiState.value = state.copy(
            selectedAnswer = answer,
            showResult = true,
            isCorrect = isCorrect,
            answeredSet = newAnswered,
            correctCount = newCorrectCount,
            answeredCount = newAnsweredCount
        )
        recordWrongIfNeeded(question.id, isCorrect)
        markAnswered(question.id, state.bankId)
        saveProgress(state.currentIndex, state.questions.size, newAnsweredCount, newCorrectCount)
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.currentIndex < state.questions.size - 1) {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedAnswer = "",
                showResult = false,
                isCorrect = false,
                wrongCount = 0,
                isMultiSelected = emptySet()
            )
            loadWrongCount(state.questions[state.currentIndex + 1].id)
        }
    }

    fun previousQuestion() {
        val state = _uiState.value
        if (state.currentIndex > 0) {
            _uiState.value = state.copy(
                currentIndex = state.currentIndex - 1,
                selectedAnswer = "",
                showResult = false,
                isCorrect = false,
                wrongCount = 0,
                isMultiSelected = emptySet()
            )
            loadWrongCount(state.questions[state.currentIndex - 1].id)
        }
    }

    fun jumpToQuestion(index: Int) {
        val state = _uiState.value
        if (index in state.questions.indices) {
            _uiState.value = state.copy(
                currentIndex = index,
                selectedAnswer = "",
                showResult = false,
                isCorrect = false,
                wrongCount = 0,
                isMultiSelected = emptySet(),
                showJumpDialog = false
            )
            loadWrongCount(state.questions[index].id)
        }
    }

    fun toggleJumpDialog() {
        _uiState.value = _uiState.value.copy(showJumpDialog = !_uiState.value.showJumpDialog)
    }

    /**
     * Save a practice record and clear progress when finishing.
     * Called when user explicitly finishes or navigates away.
     */
    fun finishPractice(onFinished: () -> Unit = {}) {
        val state = _uiState.value
        if (state.isFinishing) return
        _uiState.value = state.copy(isFinishing = true)

        viewModelScope.launch {
            val modeLabel = when (state.mode) {
                "sequential" -> "顺序练习"
                "random" -> "随机刷题"
                "wrong" -> "错题重做"
                "type_SINGLE" -> "单选题练习"
                "type_MULTI" -> "多选题练习"
                "type_JUDGE" -> "判断题练习"
                else -> state.mode
            }

            val record = PracticeRecordEntity(
                id = if (state.recordId > 0) state.recordId else 0,
                bankId = state.bankId,
                mode = state.mode,
                modeLabel = modeLabel,
                totalCount = state.questions.size,
                answeredCount = state.answeredCount,
                correctCount = state.correctCount,
                wrongCount = state.answeredCount - state.correctCount,
                isCompleted = state.answeredCount >= state.questions.size,
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis()
            )

            if (state.recordId > 0) {
                // Update existing record (from profile → resume)
                quizRepository.updatePracticeRecord(record)
            } else {
                // Insert new record (fresh practice)
                quizRepository.insertPracticeRecord(record)
            }

            // Clear progress if completed
            if (state.answeredCount >= state.questions.size) {
                quizRepository.clearPracticeProgress(state.bankId, state.mode)
            }

            _uiState.value = _uiState.value.copy(isFinishing = false)
            onFinished()
        }
    }

    private fun loadWrongCount(questionId: Long) {
        viewModelScope.launch {
            val count = quizRepository.getWrongCount(questionId)
            _uiState.value = _uiState.value.copy(wrongCount = count)
        }
    }

    private fun recordWrongIfNeeded(questionId: Long, isCorrect: Boolean) {
        if (isCorrect) return
        viewModelScope.launch {
            val existingCount = quizRepository.getWrongCount(questionId)
            quizRepository.upsertWrongRecord(
                WrongRecordEntity(
                    questionId = questionId,
                    wrongCount = existingCount + 1,
                    lastWrongTime = System.currentTimeMillis(),
                    isRemoved = false
                )
            )
            _uiState.value = _uiState.value.copy(wrongCount = existingCount + 1)
        }
    }

    private fun markAnswered(questionId: Long, bankId: Long) {
        viewModelScope.launch {
            quizRepository.markQuestionAnswered(questionId, bankId)
        }
    }

    private fun saveProgress(currentIndex: Int, totalQuestions: Int, answeredCount: Int, correctCount: Int) {
        val state = _uiState.value
        if (state.mode == "exam") return // Don't save exam progress here
        viewModelScope.launch {
            quizRepository.savePracticeProgress(
                PracticeProgressEntity(
                    bankId = state.bankId,
                    mode = state.mode,
                    currentIndex = if (currentIndex < totalQuestions - 1) currentIndex + 1 else currentIndex,
                    totalQuestions = totalQuestions,
                    answeredCount = answeredCount,
                    correctCount = correctCount,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
