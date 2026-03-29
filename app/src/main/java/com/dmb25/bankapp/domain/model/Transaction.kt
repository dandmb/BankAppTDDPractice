package com.dmb25.bankapp.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val category: Category,
    val date: LocalDateTime? = LocalDateTime.now(),
    val accountId: Long
)
