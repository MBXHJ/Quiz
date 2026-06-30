package com.quizapp.ui.screens.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.ExamRecordEntity
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.db.entity.WrongRecordEntity
import com.quizapp.data.repository.QuizRepository
import com.quizapp.util.normalizeAnswer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

data class ExamUiState(
    val questions: List<QuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<Long, String> = emptyMap(),
    val multiSelections: Map<Long, Set<String>> = emptyMap(),
    val isFinished: Boolean = false,
    val timeRemaining: Long = 0L,
    val totalCount: Int = 0
)

data class ExamConfig(
    val single: Int = 70,
    val multi: Int = 20,
    val judge: Int = 10
)

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()
    private val json = Json { ignoreUnknownKeys = true }

    fun loadExam(bankId: Long) {
        viewModelScope.launch {
            val bank = quizRepository.getBankByIdOnce(bankId)
            val config = parseConfig(bank?.examConfig ?: "")
            val questions = generateExamQuestions(bankId, config)
            _uiState.value = _uiState.value.copy(questions = questions, totalCount = questions.size)
        }
    }

    private fun parseConfig(configStr: String): ExamConfig {
        return try {
            if (configStr.isBlank()) return ExamConfig()
            val obj = json.parseToJsonElement(configStr).jsonObject
            ExamConfig(
                single = obj["SINGLE"]?.jsonPrimitive?.content?.toIntOrNull() ?: 70,
                multi = obj["MULTI"]?.jsonPrimitive?.content?.toIntOrNull() ?: 20,
                judge = obj["JUDGE"]?.jsonPrimitive?.content?.toIntOrNull() ?: 10
            )
        } catch (e: Exception) { ExamConfig() }
    }

    data class CountResult(val single: Int, val multi: Int, val judge: Int)

    suspend fun loadStockCount(bankId: Long): CountResult {
        val all = quizRepository.getAllQuestionsOnce(bankId)
        return CountResult(
            single = all.count { it.questionType == "SINGLE" },
            multi = all.count { it.questionType == "MULTI" },
            judge = all.count { it.questionType == "JUDGE" }
        )
    }

    private suspend fun generateExamQuestions(bankId: Long, config: ExamConfig): List<QuestionEntity> {
        val stock = loadStockCount(bankId)
        val result = mutableListOf<QuestionEntity>()

        val targets = mapOf(
            "SINGLE" to config.single,
            "MULTI" to config.multi,
            "JUDGE" to config.judge
        )
        for ((type, wanted) in targets) {
            val available = when (type) {
                "SINGLE" -> stock.single; "MULTI" -> stock.multi; "JUDGE" -> stock.judge; else -> 0
            }
            val take = minOf(wanted, available)
            if (take > 0) result.addAll(quizRepository.getRandomQuestionsByType(bankId, type, take))
        }
        return result.shuffled()
    }

    fun selectAnswer(questionId: Long, answer: String) {
        val question = _uiState.value.questions.find { it.id == questionId } ?: return
        if (question.questionType == "MULTI") {
            val cur = _uiState.value.multiSelections[questionId] ?: emptySet()
            val next = if (answer in cur) cur - answer else cur + answer
            _uiState.value = _uiState.value.copy(multiSelections = _uiState.value.multiSelections + (questionId to next))
        } else {
            _uiState.value = _uiState.value.copy(answers = _uiState.value.answers + (questionId to answer))
        }
    }

    fun nextQuestion() { _uiState.value.let { s -> if (s.currentIndex < s.questions.size - 1) _uiState.value = s.copy(currentIndex = s.currentIndex + 1) } }

    fun previousQuestion() { _uiState.value.let { s -> if (s.currentIndex > 0) _uiState.value = s.copy(currentIndex = s.currentIndex - 1) } }

    fun finishExam(onResult: (score: Int, total: Int, correct: Int, examRecordId: Long) -> Unit) {
        val s = _uiState.value
        if (s.questions.isEmpty()) {
            onResult(0, 0, 0, -1L)
            return
        }

        var correct = 0
        val wrongQuestionIds = mutableListOf<Long>()

        // Batch all DB operations into a single coroutine to avoid race conditions
        viewModelScope.launch {
            for (q in s.questions) {
                val ua = if (q.questionType == "MULTI") (s.multiSelections[q.id]?.sorted()?.joinToString("") ?: "") else s.answers[q.id] ?: ""
                val isCorrect = normalizeAnswer(ua, q.questionType) == normalizeAnswer(q.answer, q.questionType)
                if (isCorrect) correct++ else wrongQuestionIds.add(q.id)

                if (ua.isNotBlank()) {
                    quizRepository.markQuestionAnswered(q.id, q.bankId)
                    if (!isCorrect) {
                        val existingCount = quizRepository.getWrongCount(q.id)
                        quizRepository.upsertWrongRecord(
                            WrongRecordEntity(
                                questionId = q.id,
                                wrongCount = existingCount + 1,
                                lastWrongTime = System.currentTimeMillis(),
                                isRemoved = false
                            )
                        )
                        quizRepository.scheduleReview(q.id)
                    }
                }
            }

            val score = if (s.questions.isNotEmpty()) (correct * 100) / s.questions.size else 0
            val wrongIdsJson = wrongQuestionIds.joinToString(",")

            val recordId = quizRepository.insertRecord(
                ExamRecordEntity(
                    bankId = s.questions.first().bankId,
                    score = score,
                    totalCount = s.questions.size,
                    correctCount = correct,
                    questionDetails = wrongIdsJson,
                    examDate = System.currentTimeMillis()
                )
            )
            // Update daily stats
            val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            quizRepository.incrementDailyAnswered(todayDate, 0L)
            onResult(score, s.questions.size, correct, recordId)
        }
    }
}
