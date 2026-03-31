package com.dmb25.bankapp.data.mapper

import com.dmb25.bankapp.data.local.entity.TransactionEntity
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import java.time.LocalDateTime

object TransactionMapper {

    fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            amount = amount,
            description = description,
            type = type.name,
            categoryId = category.id,
            date = date.toString(),
            accountId = accountId
        )
    }

    fun TransactionEntity.toDomain(category: Category): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            description = description,
            type = TransactionType.valueOf(type),
            category = category,
            date = LocalDateTime.parse(date),
            accountId = accountId
        )
    }
}