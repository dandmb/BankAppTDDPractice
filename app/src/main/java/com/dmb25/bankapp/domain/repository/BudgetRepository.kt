package com.dmb25.bankapp.domain.repository

import com.dmb25.bankapp.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun setBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>>
}