package com.dmb25.bankapp.domain.model

data class Account(
    val id: Long = 0,
    val name: String,
    val balance: Double = 0.0,
    val currency: String = "EUR"
)