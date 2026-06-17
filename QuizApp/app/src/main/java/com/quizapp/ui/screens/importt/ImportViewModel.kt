package com.quizapp.ui.screens.importt

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.repository.ImportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

data class ImportUiState(
    val bankName: String = "",
    val isImporting: Boolean = false,
    val isComplete: Boolean = false,
    val successCount: Int = 0,
    val failCount: Int = 0,
    val error: String? = null,
    val hasCustomExamConfig: Boolean = false,
    val singleWeight: String = "70",
    val multiWeight: String = "20",
    val judgeWeight: String = "10"
)

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val importRepository: ImportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()
    private val json = Json { ignoreUnknownKeys = true }

    fun updateBankName(name: String) {
        _uiState.value = _uiState.value.copy(bankName = name)
    }

    fun toggleCustomExamConfig() {
        _uiState.value = _uiState.value.copy(
            hasCustomExamConfig = !_uiState.value.hasCustomExamConfig
        )
    }

    fun updateWeights(single: String, multi: String, judge: String) {
        _uiState.value = _uiState.value.copy(
            singleWeight = single, multiWeight = multi, judgeWeight = judge
        )
    }

    fun importFile(uri: Uri, fileName: String) {
        val state = _uiState.value
        if (state.bankName.isBlank()) {
            _uiState.value = state.copy(error = "请输入题库名称")
            return
        }

        val examConfig = if (state.hasCustomExamConfig) {
            buildJsonObject {
                put("SINGLE", state.singleWeight.toIntOrNull() ?: 70)
                put("MULTI", state.multiWeight.toIntOrNull() ?: 20)
                put("JUDGE", state.judgeWeight.toIntOrNull() ?: 10)
            }.toString()
        } else ""

        viewModelScope.launch {
            _uiState.value = state.copy(isImporting = true, error = null)
            try {
                val result = importRepository.importFromUri(
                    state.bankName, uri, fileName, examConfig
                )
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    isComplete = true,
                    successCount = result.successCount,
                    failCount = result.failCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    error = "导入失败: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun reset() {
        _uiState.value = ImportUiState()
    }
}
