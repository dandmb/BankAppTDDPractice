package com.dmb25.bankapp.domain.usecase

import app.cash.turbine.test
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.domain.repository.TransactionRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetTransactionsUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetTransactionsUseCase

    private val category = Category(
        id = 1L,
        name = "Food",
        iconName = "restaurant",
        colorHex = "#FF5722"
    )

    private fun makeTransaction(
        id: Long = 1L,
        amount: Double = 100.0,
        type: TransactionType = TransactionType.EXPENSE,
        accountId: Long = 1L,
        date: LocalDateTime = LocalDateTime.now()
    ) = Transaction(
        id = id,
        amount = amount,
        description = "Test",
        type = type,
        category = category,
        date = date,
        accountId = accountId
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetTransactionsUseCase(repository)
    }

    @Test
    fun `quand 3 transactions existent, la liste contient 3 elements`() = runTest {

        val transactions = listOf(
            makeTransaction(id = 1L, amount = 1000.0, type = TransactionType.INCOME),
            makeTransaction(id = 2L, amount = 100.0, type = TransactionType.EXPENSE),
            makeTransaction(id = 3L, amount = 10.0, type = TransactionType.EXPENSE)
        )
        every { repository.getTransactionsByAccount(1L) } returns flowOf(transactions)

        useCase(accountId = 1L).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            awaitComplete()
        }
    }

    @Test
    fun `quand aucune transaction, la liste est vide`() = runTest {

        every { repository.getTransactionsByAccount(1L) } returns flowOf(emptyList())

        useCase(accountId = 1L).test {
            val result = awaitItem()
            assertEquals(0, result.size)
            awaitComplete()
        }
    }

    @Test
    fun `quand accountId valide, seules les transactions du compte sont retournees`() = runTest {

        val transactions = listOf(
            makeTransaction(id = 1L, accountId = 1L),
            makeTransaction(id = 2L, accountId = 1L)
        )
        every { repository.getTransactionsByAccount(1L) } returns flowOf(transactions)

        useCase(accountId = 1L).test {
            val result = awaitItem()
            // toutes les transactions appartiennent au compte 1
            assertEquals(true, result.all { it.accountId == 1L })
            awaitComplete()
        }
    }

    @Test
    fun `quand plusieurs transactions, elles sont triees par date decroissante`() = runTest {
        val dateAncienne = LocalDateTime.of(2024, 1, 1, 0, 0)
        val dateMoyenne = LocalDateTime.of(2024, 6, 1, 0, 0)
        val dateRecente = LocalDateTime.of(2024, 12, 1, 0, 0)

        val transactions = listOf(
            makeTransaction(id = 1L, date = dateMoyenne),
            makeTransaction(id = 2L, date = dateAncienne),
            makeTransaction(id = 3L, date = dateRecente)
        )
        every { repository.getTransactionsByAccount(1L) } returns flowOf(transactions)

        useCase(accountId = 1L).test {
            val result = awaitItem()
            // la plus récente doit être en premier
            assertEquals(dateRecente, result[0].date)
            assertEquals(dateMoyenne, result[1].date)
            assertEquals(dateAncienne, result[2].date)
            awaitComplete()
        }
    }

    @Test
    fun `quand accountId est zero, une exception est lancee`() {

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(accountId = 0L).collect {} }
        }
        assertEquals(GetTransactionsUseCase.INVALID_ACCOUNT_ID, exception.message)
    }

    @Test
    fun `quand accountId est zero, le repository n est jamais appele`() {

        runCatching {
            runBlocking { useCase(accountId = 0L).collect {} }
        }

        coVerify(exactly = 0) { repository.getTransactionsByAccount(any()) }
    }
}