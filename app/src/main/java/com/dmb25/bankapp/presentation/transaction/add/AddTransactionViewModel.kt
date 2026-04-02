package com.dmb25.bankapp.presentation.transaction.add

import androidx.lifecycle.ViewModel
import com.dmb25.bankapp.domain.usecase.AddTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddTransactionViewModel(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle())
    val uiState : StateFlow<AddTransactionUiState> = _uiState.asStateFlow()
}