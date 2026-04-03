package com.dmb25.bankapp.presentation.transaction.add

import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.TransactionType

sealed class AddTransactionUiState {
    object Idle : AddTransactionUiState()
    object Loading : AddTransactionUiState()
    object Success : AddTransactionUiState()
    data class Error(val message: String) : AddTransactionUiState()
}