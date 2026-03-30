package com.dmb25.bankapp.domain.usecase

import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetTransactionsUseCase(
    private val repository: TransactionRepository
) {

    operator fun invoke(accountId: Long): Flow<List<Transaction>> {
        validate(accountId)
        return repository.getTransactionsByAccount(accountId)
            .map { transactions ->
                transactions.sortedByDescending { it.date }
            }
    }

    private fun validate(accountId: Long) {
        require(accountId != 0L) { INVALID_ACCOUNT_ID }
    }

    companion object {
        const val INVALID_ACCOUNT_ID = "Invalid account id"
    }
}