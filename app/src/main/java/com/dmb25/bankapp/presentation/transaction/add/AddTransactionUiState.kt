package com.dmb25.bankapp.presentation.transaction.add

import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.TransactionType

sealed class AddTransactionUiState {
    data class Idle(
        val amount:String = "",
        val description:String = "",
        val type: TransactionType = TransactionType.EXPENSE,
        val selectedCategory: Category? = null,
        val amountError: String? = "",
        val descriptionError: String? = ""
        ) : AddTransactionUiState()
    object Loading : AddTransactionUiState()
    object Success : AddTransactionUiState()
    data class Error(val message: String) : AddTransactionUiState()
}