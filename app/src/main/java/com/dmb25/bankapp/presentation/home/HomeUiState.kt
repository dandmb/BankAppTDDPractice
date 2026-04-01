package com.dmb25.bankapp.presentation.home

import com.dmb25.bankapp.domain.model.Transaction

sealed class HomeUiState{
    object Loading : HomeUiState()
    data class Success(
        val balance: Double,
        val transactions: List<Transaction>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}