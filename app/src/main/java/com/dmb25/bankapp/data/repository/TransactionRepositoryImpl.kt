package com.dmb25.bankapp.data.repository;

import com.dmb25.bankapp.data.local.dao.TransactionDao;
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository{
    override suspend fun addTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        TODO("Not yet implemented")
    }

    override fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        TODO("Not yet implemented")
    }

}