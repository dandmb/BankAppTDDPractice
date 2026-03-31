package com.dmb25.bankapp.data.repository;

import com.dmb25.bankapp.data.local.dao.TransactionDao
import com.dmb25.bankapp.data.mapper.TransactionMapper.toDomain
import com.dmb25.bankapp.data.mapper.TransactionMapper.toEntity
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        val entity = transactionDao.getById(id)
        return entity?.toDomain(
            category = Category(
                id = entity.categoryId,
                name = "",
                iconName = "",
                colorHex = ""
            )
        )
    }

    override fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>> {
        return transactionDao.getByAccount(accountId).map { list ->
            list.map { entity ->
                entity.toDomain(
                    category = Category(
                        id = entity.categoryId,
                        name = "",
                        iconName = "",
                        colorHex = ""
                    )
                )
            }
        }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAll().map { list ->
            list.map { entity ->
                entity.toDomain(
                    category = Category(
                        id = entity.categoryId,
                        name = "",
                        iconName = "",
                        colorHex = ""
                    )
                )
            }
        }
    }
}