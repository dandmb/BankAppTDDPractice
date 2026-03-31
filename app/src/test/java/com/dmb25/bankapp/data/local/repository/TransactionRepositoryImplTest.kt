package com.dmb25.bankapp.data.local.repository

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import com.dmb25.bankapp.data.local.dao.TransactionDao
import com.dmb25.bankapp.data.local.entity.TransactionEntity
import com.dmb25.bankapp.data.repository.TransactionRepositoryImpl
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class TransactionRepositoryImplTest {

    private lateinit var dao: TransactionDao
    private lateinit var repository: TransactionRepositoryImpl

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
        date = "2024-01-01T00:00:00",
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

    @Before
    fun setup() {
        dao = mockk()
        repository = TransactionRepositoryImpl(dao)
    }

    @Test
    fun whenAddTransaction_daoInsertIsCalledOnce() = runTest {
        coEvery { dao.insert(any()) } returns 1L
        repository.addTransaction(transaction)
        coVerify(exactly = 1) { dao.insert(any()) }
    }

    @Test
    fun whenAddTransaction_correctEntityIsPassedToDao() = runTest {
        val slot = slot<TransactionEntity>()

        coEvery { dao.insert(capture(slot)) } returns 1L
        repository.addTransaction(transaction)

        assertEquals(transaction.id, slot.captured.id)
        assertEquals(transaction.amount, slot.captured.amount, 0.0)
        assertEquals(transaction.description, slot.captured.description)
        assertEquals("EXPENSE", slot.captured.type)
    }

    @Test
    fun whenInsertFails_exceptionIsPropagated() {
        coEvery { dao.insert(any()) } throws RuntimeException("DB error")

        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking { repository.addTransaction(transaction) }
        }
        assertEquals("DB error", exception.message)
    }

    @Test
    fun whenInsertFails_daoIsCalledOnce() {
        coEvery { dao.insert(any()) } throws RuntimeException("DB error")

        runCatching { runBlocking { repository.addTransaction(transaction) } }

        coVerify(exactly = 1) { dao.insert(any()) }
    }


    @Test
    fun whenDeleteTransaction_daoDeleteIsCalledOnce() = runTest {
        coEvery { dao.delete(any()) } returns 1

        repository.deleteTransaction(transaction)

        coVerify(exactly = 1) { dao.delete(any()) }
    }

    @Test
    fun whenDeleteTransaction_correctEntityIsPassedToDao() = runTest {
        val slot = slot<TransactionEntity>()
        coEvery { dao.delete(capture(slot)) } returns 1

        repository.deleteTransaction(transaction)

        assertEquals(transaction.id, slot.captured.id)
        assertEquals(transaction.amount, slot.captured.amount, 0.0)
        assertEquals(transaction.description, slot.captured.description)
    }

    @Test
    fun whenDeleteFails_exceptionIsPropagated() {
        coEvery { dao.delete(any()) } throws RuntimeException("DB error")

        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking { repository.deleteTransaction(transaction) }
        }
        assertEquals("DB error", exception.message)
    }


    @Test
    fun whenTransactionFound_correctFieldsAreReturned() = runTest {
        coEvery { dao.getById(1L) } returns transactionEntity

        val result = repository.getTransactionById(1L)

        assertEquals(transactionEntity.id, result?.id)
        assertEquals(transactionEntity.amount, result?.amount)
        assertEquals(TransactionType.EXPENSE, result?.type)
    }

    @Test
    fun whenTransactionNotFound_nullIsReturned() = runTest {
        coEvery { dao.getById(99L) } returns null

        val result = repository.getTransactionById(99L)

        assertNull(result)
    }

    @Test
    fun whenGetById_daoIsCalledOnce() = runTest {
        coEvery { dao.getById(any()) } returns transactionEntity

        repository.getTransactionById(1L)

        coVerify(exactly = 1) { dao.getById(1L) }
    }


    @Test
    fun whenGetByAccount_flowEmitsCorrectSize() = runTest {
        every { dao.getByAccount(1L) } returns flowOf(listOf(transactionEntity))

        repository.getTransactionsByAccount(1L).test {
            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGetByAccountIsEmpty_flowEmitsEmptyList() = runTest {
        every { dao.getByAccount(1L) } returns flowOf(emptyList())

        repository.getTransactionsByAccount(1L).test {
            assertEquals(0, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGetByAccountFails_errorIsPropagatedInFlow() = runTest {
        every { dao.getByAccount(1L) } returns flow {
            throw RuntimeException("DB error")
        }

        repository.getTransactionsByAccount(1L).test {
            val error = awaitError()
            assertEquals("DB error", error.message)
        }
    }

    @Test
    fun whenGetAll_flowEmitsCorrectSize() = runTest {
        every { dao.getAll() } returns flowOf(listOf(transactionEntity, transactionEntity))

        repository.getAllTransactions().test {
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGetAllIsEmpty_flowEmitsEmptyList() = runTest {
        every { dao.getAll() } returns flowOf(emptyList())

        repository.getAllTransactions().test {
            assertEquals(0, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}