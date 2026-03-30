package com.dmb25.bankapp.domain.usecase

import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke (transaction: Transaction) {
        validate(transaction)
        repository.deleteTransaction(transaction)
    }

    fun validate(transaction: Transaction) {
        require(transaction.id != 0L) { INVALID_TRANSACTION_ID }
    }

    companion object {
        const val INVALID_TRANSACTION_ID = "L'id de la transaction ne peut pas etre 0"

    }
}