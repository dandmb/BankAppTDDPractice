package com.dmb25.bankapp.data.local.mapper

import com.dmb25.bankapp.data.local.entity.TransactionEntity
import com.dmb25.bankapp.data.mapper.TransactionMapper.toDomain
import com.dmb25.bankapp.data.mapper.TransactionMapper.toEntity
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime

class TransactionMapperTest {

    private val category = Category(
        id = 1L,
        name = "Food",
        iconName = "restaurant",
        colorHex = "#FF5722"
    )

    private val transactionEntity = TransactionEntity(
        id = 1L,
        amount = 100.0,
        description = "Test",
        type = "EXPENSE",
        categoryId = 1L,
        date = "2024-01-01T00:00",
        accountId = 1L
    )

    private val transaction = Transaction(
        id = 1L,
        amount = 100.0,
        description = "Test",
        type = TransactionType.EXPENSE,
        category = category,
        date = LocalDateTime.of(2024, 1, 1, 0, 0),
        accountId = 1L
    )

    @Test
    fun whenEntityGiven_toDomainReturnsCorrectId() {
        val result = transactionEntity.toDomain(category)
        assertEquals(1L, result.id)
    }

    @Test
    fun whenEntityGiven_toDomainReturnsCorrectAmount() {
        val result = transactionEntity.toDomain(category)
        assertEquals(100.0, result.amount, 0.0)
    }

    @Test
    fun whenEntityGiven_toDomainReturnsCorrectDescription() {
        val result = transactionEntity.toDomain(category)
        assertEquals("Test", result.description)
    }

    @Test
    fun whenEntityGiven_toDomainReturnsCorrectType() {
        val result = transactionEntity.toDomain(category)
        assertEquals(TransactionType.EXPENSE, result.type)
    }

    @Test
    fun whenEntityGiven_toDomainReturnsCorrectDate() {
        val result = transactionEntity.toDomain(category)
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0), result.date)
    }

    @Test
    fun whenEntityGiven_toDomainReturnsCorrectAccountId() {
        val result = transactionEntity.toDomain(category)
        assertEquals(1L, result.accountId)
    }

    @Test
    fun whenEntityGiven_toDomainReturnsCorrectCategory() {
        val result = transactionEntity.toDomain(category)
        assertEquals(category.id, result.category.id)
        assertEquals(category.name, result.category.name)
        assertEquals(category.colorHex, result.category.colorHex)
    }

    @Test
    fun whenEntityGiven_toDomainResultIsNotNull() {
        val result = transactionEntity.toDomain(category)
        assertNotNull(result)
    }

    @Test
    fun whenIncomeEntityGiven_toDomainReturnsIncomeType() {
        val incomeEntity = transactionEntity.copy(type = "INCOME")
        val result = incomeEntity.toDomain(category)
        assertEquals(TransactionType.INCOME, result.type)
    }


    @Test
    fun whenTransactionGiven_toEntityReturnsCorrectId() {
        val result = transaction.toEntity()
        assertEquals(1L, result.id)
    }

    @Test
    fun whenTransactionGiven_toEntityReturnsCorrectAmount() {
        val result = transaction.toEntity()
        assertEquals(100.0, result.amount, 0.0)
    }

    @Test
    fun whenTransactionGiven_toEntityReturnsCorrectDescription() {
        val result = transaction.toEntity()
        assertEquals("Test", result.description)
    }

    @Test
    fun whenTransactionGiven_toEntityReturnsTypeAsString() {
        val result = transaction.toEntity()
        assertEquals("EXPENSE", result.type)
    }

    @Test
    fun whenTransactionGiven_toEntityReturnsDateAsString() {
        val result = transaction.toEntity()
        assertEquals("2024-01-01T00:00", result.date)
    }

    @Test
    fun whenTransactionGiven_toEntityReturnsCategoryId() {
        val result = transaction.toEntity()
        assertEquals(1L, result.categoryId)
    }

    @Test
    fun whenTransactionGiven_toEntityReturnsCorrectAccountId() {
        val result = transaction.toEntity()
        assertEquals(1L, result.accountId)
    }

    @Test
    fun whenTransactionGiven_toEntityResultIsNotNull() {
        val result = transaction.toEntity()
        assertNotNull(result)
    }

    @Test
    fun whenIncomeTransactionGiven_toEntityReturnsIncomeString() {
        val incomeTransaction = transaction.copy(type = TransactionType.INCOME)
        val result = incomeTransaction.toEntity()
        assertEquals("INCOME", result.type)
    }


    @Test
    fun whenEntityConvertedToDomainAndBack_resultIsEqual() {
        val domain = transactionEntity.toDomain(category)
        val backToEntity = domain.toEntity()
        assertEquals(transactionEntity.id, backToEntity.id)
        assertEquals(transactionEntity.amount, backToEntity.amount, 0.0)
        assertEquals(transactionEntity.type, backToEntity.type)
        assertEquals(transactionEntity.date, backToEntity.date)
        assertEquals(transactionEntity.accountId, backToEntity.accountId)
    }
}
