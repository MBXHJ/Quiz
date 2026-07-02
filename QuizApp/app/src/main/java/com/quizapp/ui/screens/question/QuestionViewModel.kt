package com.quizapp.ui.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.db.entity.WrongRecordEntity
import com.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.quizapp.util.normalizeAnswer
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
    val recordId: Long = -1L,
    val wrongQuestionIds: List<Long> = emptyList(),
    val isFavorite: Boolean = false,
    val isMarked: Boolean = false,
    val elapsedSeconds: Int = 0,
    val startTime: Long = 0L,
    val showNoteDialog: Boolean = false,
    val noteText: String = "",
    // Tags
    val questionTagIds: Set<Long> = emptySet(),
    val allTags: List<com.quizapp.data.db.entity.TagEntity> = emptyList(),
    val showTagDialog: Boolean = false,
    // TTS
    val isSpeaking: Boolean = false
)

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionUiState())
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

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
                mode.startsWith("record_wrong_") -> {
                    // mode = "record_wrong_<recordId>" or "record_wrong_<id1,id2,id3>"
                    val suffix = mode.removePrefix("record_wrong_")
                    val recId = suffix.toLongOrNull()
                    if (recId != null) {
                        val rec = quizRepository.getPracticeRecordById(recId)
                        if (rec != null && rec.wrongQuestionIds.isNotBlank()) {
                            val ids = rec.wrongQuestionIds.split(",").mapNotNull { it.trim().toLongOrNull() }
                            quizRepository.getQuestionsByIdsOnce(ids)
                        } else emptyList()
                    } else {
                        // Direct comma-separated question IDs (used for exam review)
                        val ids = suffix.split(",").mapNotNull { it.trim().toLongOrNull() }
                        quizRepository.getQuestionsByIdsOnce(ids)
                    }
                }
                mode.startsWith("exam_review_") -> {
                    // mode = "exam_review_<examRecordId>" — load wrong questions from exam record
                    val examRecordId = mode.removePrefix("exam_review_").toLongOrNull() ?: 0
                    val record = quizRepository.getExamRecordById(examRecordId)
                    if (record != null && record.questionDetails.isNotBlank()) {
                        val ids = record.questionDetails.split(",").mapNotNull { it.trim().toLongOrNull() }
                        quizRepository.getQuestionsByIdsOnce(ids)
                    } else emptyList()
                }
                mode == "review" -> {
                    val dueQuestions = quizRepository.getDueReviewQuestionsByBank(bankId, System.currentTimeMillis())
                    dueQuestions
                }
                mode == "favorite" -> {
                    val ids = quizRepository.getAllFavoriteIds()
                    quizRepository.getQuestionsByIdsOnce(ids)
                }
                mode == "marked" -> {
                    val ids = quizRepository.getAllMarkedIds()
                    quizRepository.getQuestionsByIdsOnce(ids)
                }
                mode.startsWith("search_") -> {
                    val query = mode.removePrefix("search_")
                    val allQuestions = quizRepository.getAllQuestionsOnce(bankId)
                    allQuestions.filter { it.content.contains(query, ignoreCase = true) }
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

            val startTime = System.currentTimeMillis()

            _uiState.value = QuestionUiState(
                questions = questions,
                currentIndex = startIndex,
                isLoading = false,
                bankId = bankId,
                mode = mode,
                recordId = recordId,
                answeredCount = restoredAnswered,
                correctCount = restoredCorrect,
                answeredSet = emptySet(),
                startTime = startTime
            )

            // Start elapsed timer
            startTimer()

            // Load favorite/mark status for the current question
            if (questions.isNotEmpty()) {
                loadFavoriteMarkStatus(questions[startIndex].id)
            }
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
            val isCorrect = normalizeAnswer(answer, question.questionType) == normalizeAnswer(question.answer, question.questionType)
            val newAnswered = state.answeredSet + state.currentIndex
            val newCorrectCount = state.correctCount + if (isCorrect) 1 else 0
            val newAnsweredCount = state.answeredCount + 1
            val newWrongIds = if (!isCorrect) state.wrongQuestionIds + question.id else state.wrongQuestionIds

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

        val isCorrect = normalizeAnswer(answer, question.questionType) == normalizeAnswer(question.answer, question.questionType)
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
            loadFavoriteMarkStatus(state.questions[state.currentIndex + 1].id)
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
            loadFavoriteMarkStatus(state.questions[state.currentIndex - 1].id)
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
            loadFavoriteMarkStatus(state.questions[index].id)
        }
    }

    fun toggleJumpDialog() {
        _uiState.value = _uiState.value.copy(showJumpDialog = !_uiState.value.showJumpDialog)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val state = _uiState.value
            val question = state.questions.getOrNull(state.currentIndex) ?: return@launch
            val isFav = quizRepository.isFavorite(question.id)
            if (isFav) {
                quizRepository.removeFavorite(question.id)
            } else {
                quizRepository.addFavorite(question.id)
            }
            _uiState.value = _uiState.value.copy(isFavorite = !isFav)
        }
    }

    fun toggleMark() {
        viewModelScope.launch {
            val state = _uiState.value
            val question = state.questions.getOrNull(state.currentIndex) ?: return@launch
            val isMkd = quizRepository.isMarked(question.id)
            if (isMkd) {
                quizRepository.removeMark(question.id)
            } else {
                quizRepository.addMark(question.id)
            }
            _uiState.value = _uiState.value.copy(isMarked = !isMkd)
        }
    }

    fun toggleNoteDialog() {
        viewModelScope.launch {
            val q = _uiState.value.questions.getOrNull(_uiState.value.currentIndex) ?: return@launch
            val note = quizRepository.getNote(q.id)
            _uiState.value = _uiState.value.copy(showNoteDialog = !_uiState.value.showNoteDialog, noteText = note?.note ?: "")
        }
    }

    fun updateNoteText(text: String) {
        _uiState.value = _uiState.value.copy(noteText = text)
    }

    fun saveCurrentNote() {
        viewModelScope.launch {
            val q = _uiState.value.questions.getOrNull(_uiState.value.currentIndex) ?: return@launch
            if (_uiState.value.noteText.isBlank()) quizRepository.deleteNote(q.id)
            else {
                quizRepository.saveNote(q.id, _uiState.value.noteText)
                _uiState.value = _uiState.value.copy(noteText = _uiState.value.noteText)
            }
        }
    }

    /**
     * Save a practice record and clear progress when finishing.
     * Called when user explicitly finishes or navigates away.
     */
    fun finishPractice(onFinished: () -> Unit = {}) {
        val state = _uiState.value
        if (state.isFinishing || state.questions.isEmpty()) {
            onFinished()
            return
        }
        _uiState.value = state.copy(isFinishing = true)

        viewModelScope.launch {
            val modeLabel = when {
                state.mode.startsWith("record_wrong_") -> "错题重做"
                state.mode.startsWith("exam_review_") -> "考试回顾"
                state.mode == "sequential" -> "顺序练习"
                state.mode == "random" -> "随机刷题"
                state.mode == "wrong" -> "错题重做"
                state.mode == "review" -> "艾宾浩斯复习"
                state.mode == "favorite" -> "收藏练习"
                state.mode == "marked" -> "标记练习"
                state.mode.startsWith("search_") -> "搜索练习"
                state.mode == "type_SINGLE" -> "单选题练习"
                state.mode == "type_MULTI" -> "多选题练习"
                state.mode == "type_JUDGE" -> "判断题练习"
                state.mode == "type_FILL" -> "填空题练习"
                else -> state.mode
            }

            val wrongIdsJson = state.wrongQuestionIds.joinToString(",")

            val record = PracticeRecordEntity(
                id = if (state.recordId > 0) state.recordId else 0,
                bankId = state.bankId,
                mode = state.mode,
                modeLabel = modeLabel,
                totalCount = state.questions.size,
                answeredCount = state.answeredCount,
                correctCount = state.correctCount,
                wrongCount = state.answeredCount - state.correctCount,
                wrongQuestionIds = wrongIdsJson,
                isCompleted = state.answeredCount >= state.questions.size,
                startTime = if (state.startTime > 0L) state.startTime else System.currentTimeMillis(),
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

            // Stop the timer
            stopTimer()

            // Update daily stats
            try {
                val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                val elapsedMs = System.currentTimeMillis() - (if (state.startTime > 0L) state.startTime else System.currentTimeMillis())
                quizRepository.incrementDailyAnswered(todayDate, elapsedMs)
            } catch (_: Exception) { }

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

    private fun loadFavoriteMarkStatus(questionId: Long) {
        viewModelScope.launch {
            val isFav = quizRepository.isFavorite(questionId)
            val isMkd = quizRepository.isMarked(questionId)
            val tagIds = quizRepository.getTagIdsForQuestion(questionId).toSet()
            _uiState.value = _uiState.value.copy(isFavorite = isFav, isMarked = isMkd, questionTagIds = tagIds)
        }
    }

    // ===== Tag methods =====

    fun toggleTagDialog() {
        viewModelScope.launch {
            val tags = quizRepository.getAllTagsOnce()
            _uiState.value = _uiState.value.copy(showTagDialog = !_uiState.value.showTagDialog, allTags = tags)
        }
    }

    fun toggleTagOnQuestion(tagId: Long) {
        viewModelScope.launch {
            val question = _uiState.value.questions.getOrNull(_uiState.value.currentIndex) ?: return@launch
            val currentIds = _uiState.value.questionTagIds
            if (tagId in currentIds) {
                quizRepository.removeTagFromQuestion(question.id, tagId)
                _uiState.value = _uiState.value.copy(questionTagIds = currentIds - tagId)
            } else {
                quizRepository.addTagToQuestion(question.id, tagId)
                _uiState.value = _uiState.value.copy(questionTagIds = currentIds + tagId)
            }
        }
    }

    // ===== TTS methods =====

    fun speakCurrentQuestion(ttsHelper: com.quizapp.data.TtsHelper?) {
        val question = _uiState.value.questions.getOrNull(_uiState.value.currentIndex) ?: return
        val text = buildString {
            append(question.content)
            append("。")
            question.options.split("|||").filter { it.isNotBlank() }.forEachIndexed { i, opt ->
                val label = ('A' + i).toString()
                append("$label. $opt。")
            }
        }
        _uiState.value = _uiState.value.copy(isSpeaking = true)
        ttsHelper?.speak(text) {
            _uiState.value = _uiState.value.copy(isSpeaking = false)
        }
    }

    fun stopSpeaking(ttsHelper: com.quizapp.data.TtsHelper?) {
        ttsHelper?.stop()
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }

    private fun recordWrongIfNeeded(questionId: Long, isCorrect: Boolean) {
        if (isCorrect) {
            // Advance spaced repetition if in review mode
            viewModelScope.launch {
                val schedule = quizRepository.getScheduleForQuestion(questionId)
                if (schedule != null) {
                    quizRepository.advanceReview(questionId)
                }
            }
            return
        }
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

            // Schedule/reset spaced repetition
            quizRepository.scheduleReview(questionId)
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

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.value = _uiState.value.copy(elapsedSeconds = _uiState.value.elapsedSeconds + 1)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
