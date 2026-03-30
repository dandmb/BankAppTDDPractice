package com.dmb25.bankapp.domain.repository

import com.dmb25.bankapp.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun getTransactionById(id: Long): Transaction?
    fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>>
    fun getAllTransactions(): Flow<List<Transaction>>
}