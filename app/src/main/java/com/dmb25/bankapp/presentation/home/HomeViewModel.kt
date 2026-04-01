package com.dmb25.bankapp.presentation.home

import androidx.lifecycle.ViewModel
import com.dmb25.bankapp.domain.usecase.GetBalanceUseCase
import com.dmb25.bankapp.domain.usecase.GetTransactionsUseCase

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getBalanceUseCase: GetBalanceUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val accountId = 1L

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {

                combine(
                    getBalanceUseCase(accountId),
                    getTransactionsUseCase(accountId)
                ) { balance, transactions ->
                    HomeUiState.Success(
                        balance = balance,
                        transactions = transactions
                    )
                }.collect { successState ->
                    _uiState.value = successState
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.message ?: "Une erreur est survenue"
                )
            }
        }
    }
}