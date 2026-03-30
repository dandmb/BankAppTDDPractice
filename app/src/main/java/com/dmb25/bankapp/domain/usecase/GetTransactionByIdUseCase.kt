package com.dmb25.bankapp.domain.usecase

import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.repository.TransactionRepository

class GetTransactionByIdUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: Long): Transaction? {
        validateTransactionId(transactionId)
        return repository.getTransactionById(transactionId)
    }

    private fun validateTransactionId(transactionId: Long) {
        require(transactionId != 0L) { INVALID_TRANSACTION_ID }
    }

    companion object {
        const val INVALID_TRANSACTION_ID = "Invalid transaction id"
    }
}