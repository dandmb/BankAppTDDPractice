package com.dmb25.bankapp.domain.usecase

import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class AddTransactionUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: AddTransactionUseCase

    private val testCategory = Category(
        id = 1L,
        name = "Food",
        iconName = "restaurant",
        colorHex = "#FF5722"
    )

    private val validTransaction = Transaction(
        amount = 50.0,
        description = "Supermarché",
        type = TransactionType.EXPENSE,
        category = testCategory,
        date = LocalDateTime.now(),
        accountId = 1L
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddTransactionUseCase(repository)
    }

    @Test
    fun `quand transaction valide, elle est sauvegardee`() = runTest {
        // ARRANGE
        coEvery { repository.addTransaction(any()) } returns Unit

        // ACT
        useCase(validTransaction)

        // ASSERT
        coVerify(exactly = 1) { repository.addTransaction(validTransaction) }
    }

    @Test
    fun `quand transaction valide, c est exactement la meme transaction sauvegardee`() = runTest {
        // ARRANGE
        val slot = slot<Transaction>()
        coEvery { repository.addTransaction(capture(slot)) } returns Unit

        // ACT
        useCase(validTransaction)

        // ASSERT
        assertEquals(validTransaction.amount, slot.captured.amount, 0.0)
        assertEquals(validTransaction.description, slot.captured.description)
        assertEquals(validTransaction.type, slot.captured.type)
        assertEquals(validTransaction.category, slot.captured.category)
    }

    @Test
    fun `quand transaction INCOME, le type est correctement preserve`() = runTest {
        // ARRANGE
        val slot = slot<Transaction>()
        val incomeTransaction = validTransaction.copy(
            type = TransactionType.INCOME,
            description = "Salaire",
            amount = 2500.0
        )
        coEvery { repository.addTransaction(capture(slot)) } returns Unit

        // ACT
        useCase(incomeTransaction)

        // ASSERT
        assertEquals(TransactionType.INCOME, slot.captured.type)
        assertEquals(2500.0, slot.captured.amount, 0.0)
    }

    @Test
    fun `quand montant est zero, une exception est lancee`() {
        // ARRANGE
        val transaction = validTransaction.copy(amount = 0.0)

        // ACT + ASSERT
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(transaction) }
        }
        assertEquals(AddTransactionUseCase.INVALID_AMOUNT, exception.message)
    }

    @Test
    fun `quand montant est negatif, une exception est lancee`() {
        // ARRANGE
        val transaction = validTransaction.copy(amount = -0.01)

        // ACT + ASSERT
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(transaction) }
        }
        assertEquals(AddTransactionUseCase.INVALID_AMOUNT, exception.message)
    }

    @Test
    fun `quand montant est negatif, le repository n est jamais appele`() {
        // ARRANGE
        val transaction = validTransaction.copy(amount = -100.0)

        // ACT
        runCatching { runBlocking { useCase(transaction) } }

        // ASSERT
        coVerify(exactly = 0) { repository.addTransaction(any()) }
    }


    @Test
    fun `quand description est vide, une exception est lancee`() {
        // ARRANGE
        val transaction = validTransaction.copy(description = "")

        // ACT + ASSERT
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(transaction) }
        }
        assertEquals(AddTransactionUseCase.INVALID_DESCRIPTION, exception.message)
    }

    @Test
    fun `quand description contient seulement des espaces, une exception est lancee`() {
        // ARRANGE
        val transaction = validTransaction.copy(description = "   ")

        // ACT + ASSERT
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(transaction) }
        }
        assertEquals(AddTransactionUseCase.INVALID_DESCRIPTION, exception.message)
    }


    @Test
    fun `quand le repository echoue, l exception est propagee`() = runTest {
        // ARRANGE
        coEvery { repository.addTransaction(any()) } throws RuntimeException("DB error")

        // ACT + ASSERT
        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking { useCase(validTransaction) }
        }
        assertEquals("DB error", exception.message)
    }

    @Test
    fun `quand le repository echoue, il a bien ete appele une fois`() = runTest {
        // ARRANGE
        coEvery { repository.addTransaction(any()) } throws RuntimeException("DB error")

        // ACT
        runCatching { useCase(validTransaction) }

        // ASSERT
        coVerify(exactly = 1) { repository.addTransaction(any()) }
    }
}
