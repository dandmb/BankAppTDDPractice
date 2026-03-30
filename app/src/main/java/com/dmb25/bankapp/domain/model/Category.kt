package com.dmb25.bankapp.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String = "",
    val colorHex: String = "#000000"
)
