package com.quizapp.ui.screens.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.TagEntity
import com.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TagManageUiState(
    val tags: List<TagEntity> = emptyList(),
    val tagQuestionCounts: Map<Long, Int> = emptyMap(),
    val showCreateDialog: Boolean = false,
    val newTagName: String = "",
    val selectedColor: Int = 0xFF2563EB.toInt()
)

val TAG_COLORS = listOf(
    0xFF2563EB.toInt() to "蓝",
    0xFF10B981.toInt() to "绿",
    0xFFF59E0B.toInt() to "黄",
    0xFFEF4444.toInt() to "红",
    0xFF8B5CF6.toInt() to "紫",
    0xFFEC4899.toInt() to "粉",
    0xFF06B6D4.toInt() to "青",
    0xFFF97316.toInt() to "橙"
)

@HiltViewModel
class TagManageViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TagManageUiState())
    val uiState: StateFlow<TagManageUiState> = _uiState.asStateFlow()

    fun loadTags(bankId: Long) {
        viewModelScope.launch {
            val tags = quizRepository.getAllTags().first()
            val counts = quizRepository.getTagQuestionCounts(bankId)
            _uiState.value = _uiState.value.copy(tags = tags, tagQuestionCounts = counts)
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true, newTagName = "", selectedColor = TAG_COLORS.first().first)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun updateNewTagName(name: String) {
        _uiState.value = _uiState.value.copy(newTagName = name)
    }

    fun selectColor(color: Int) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
    }

    fun createTag(bankId: Long) {
        val state = _uiState.value
        if (state.newTagName.isBlank()) return
        viewModelScope.launch {
            quizRepository.createTag(state.newTagName.trim(), state.selectedColor)
            hideCreateDialog()
            loadTags(bankId)
        }
    }

    fun deleteTag(tagId: Long, bankId: Long) {
        viewModelScope.launch {
            quizRepository.deleteTag(tagId)
            loadTags(bankId)
        }
    }
}
