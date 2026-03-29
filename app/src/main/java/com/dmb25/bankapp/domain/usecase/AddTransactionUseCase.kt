package com.dmb25.bankapp.domain.usecase

import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.repository.TransactionRepository

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(transaction: Transaction) {
        validate(transaction)
        repository.addTransaction(transaction)
    }

    private fun validate(transaction: Transaction) {
        require(transaction.amount > 0) { INVALID_AMOUNT }
        require(transaction.description.isNotBlank()) { INVALID_DESCRIPTION }
    }

    companion object {
        const val INVALID_AMOUNT = "Le montant doit être supérieur à 0"
        const val INVALID_DESCRIPTION = "La description ne peut pas être vide"
    }
}