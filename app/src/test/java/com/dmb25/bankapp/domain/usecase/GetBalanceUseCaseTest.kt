package com.dmb25.bankapp.domain.usecase

import app.cash.turbine.test
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetBalanceUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetBalanceUseCase

    private val testCategory = Category(
        id = 1L,
        name = "Food",
        iconName = "restaurant",
        colorHex = "#FF5722"
    )

    private fun makeTransaction(
        amount: Double,
        type: TransactionType,
        accountId: Long = 1L
    ) = Transaction(
        amount = amount,
        description = "Test",
        type = type,
        category = testCategory,
        date = LocalDateTime.now(),
        accountId = accountId
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetBalanceUseCase(repository)
    }


    @Test
    fun `quand aucune transaction, le solde est zero`() = runTest {
        // ARRANGE
        every { repository.getAllTransactions() } returns flowOf(emptyList())

        // ACT + ASSERT
        useCase(accountId = 1L).test {
            assertEquals(0.0, awaitItem(), 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `quand seulement des depenses, le solde est negatif`() = runTest {
        // ARRANGE
        val transactions = listOf(
            makeTransaction(100.0, TransactionType.EXPENSE),
            makeTransaction(50.0, TransactionType.EXPENSE)
        )
        every { repository.getAllTransactions() } returns flowOf(transactions)

        // ACT + ASSERT
        useCase(accountId = 1L).test {
            assertEquals(-150.0, awaitItem(), 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `quand seulement des revenus, le solde est positif`() = runTest {
        // ARRANGE
        val transactions = listOf(
            makeTransaction(2500.0, TransactionType.INCOME),
            makeTransaction(500.0, TransactionType.INCOME)
        )
        every { repository.getAllTransactions() } returns flowOf(transactions)

        // ACT + ASSERT
        useCase(accountId = 1L).test {
            assertEquals(3000.0, awaitItem(), 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `quand revenus et depenses, le solde est correctement calcule`() = runTest {
        // ARRANGE
        val transactions = listOf(
            makeTransaction(2500.0, TransactionType.INCOME),
            makeTransaction(100.0, TransactionType.EXPENSE),
            makeTransaction(500.0, TransactionType.INCOME),
            makeTransaction(50.0, TransactionType.EXPENSE)
        )
        every { repository.getAllTransactions() } returns flowOf(transactions)

        // ACT + ASSERT
        // 2500 + 500 - 100 - 50 = 2850
        useCase(accountId = 1L).test {
            assertEquals(2850.0, awaitItem(), 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `quand les transactions changent, le solde est mis a jour`() = runTest {
        // ARRANGE
        val transactions1 = listOf(
            makeTransaction(100.0, TransactionType.INCOME)
        )
        val transactions2 = listOf(
            makeTransaction(100.0, TransactionType.INCOME),
            makeTransaction(30.0, TransactionType.EXPENSE)
        )
        every { repository.getAllTransactions() } returns flow {
            emit(transactions1)
            emit(transactions2)
        }

        // ACT + ASSERT — Turbine gère les émissions multiples
        useCase(accountId = 1L).test {
            assertEquals(100.0, awaitItem(), 0.0)  // première émission
            assertEquals(70.0, awaitItem(), 0.0)   // deuxième émission
            awaitComplete()
        }
    }

    @Test
    fun `quand plusieurs comptes, seules les transactions du bon compte sont comptees`() = runTest {
        // ARRANGE
        val transactions = listOf(
            makeTransaction(1000.0, TransactionType.INCOME, accountId = 1L),
            makeTransaction(500.0, TransactionType.INCOME, accountId = 2L), // ignoré
            makeTransaction(200.0, TransactionType.EXPENSE, accountId = 1L)
        )
        every { repository.getAllTransactions() } returns flowOf(transactions)

        // ACT + ASSERT
        // 1000 - 200 = 800 (le 500 du compte 2 est ignoré)
        useCase(accountId = 1L).test {
            assertEquals(800.0, awaitItem(), 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `quand le repository echoue, l erreur est propagee dans le flow`() = runTest {
        // ARRANGE
        every { repository.getAllTransactions() } returns flow {
            throw RuntimeException("DB error")
        }

        useCase(accountId = 1L).test {
            val error = awaitError()
            assertEquals("DB error", error.message)
        }
    }
}
