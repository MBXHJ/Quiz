package com.quizapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankListViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    val banks: StateFlow<List<QuestionBankEntity>> = quizRepository.getAllBanks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteBank(bank: QuestionBankEntity) {
        viewModelScope.launch {
            quizRepository.deleteBank(bank)
        }
    }
}
