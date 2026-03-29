package com.dmb25.bankapp.domain.model

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val limitAmount: Double,
    val month: Int,
    val year: Int
)
