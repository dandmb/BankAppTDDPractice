package com.dmb25.bankapp.domain.usecase

import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBalanceUseCase(
    private val repository: TransactionRepository
) {

    operator fun invoke(accountId: Long): Flow<Double> {
        return repository.getAllTransactions()
            .map { transactions ->
                val accountTransactions = transactions.filterByAccount(accountId)
                calculateBalance(accountTransactions)
            }
    }

    private fun List<Transaction>.filterByAccount(accountId: Long): List<Transaction> {
        return filter { it.accountId == accountId }
    }

    private fun calculateBalance(transactions: List<Transaction>): Double {
        return transactions.sumOf { transaction ->
            when (transaction.type) {
                TransactionType.INCOME  ->  transaction.amount
                TransactionType.EXPENSE -> -transaction.amount
            }
        }
    }
}