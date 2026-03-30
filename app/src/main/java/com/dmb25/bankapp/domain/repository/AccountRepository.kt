package com.dmb25.bankapp.domain.repository

import com.dmb25.bankapp.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun addAccount(account: Account)
    suspend fun updateAccount(account: Account)
    fun getAccounts(): Flow<List<Account>>
    suspend fun getAccountById(id: Long): Account?
}