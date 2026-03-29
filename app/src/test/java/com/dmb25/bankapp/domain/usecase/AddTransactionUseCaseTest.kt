package com.dmb25.bankapp.domain.usecase

import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
        coEvery { repository.addTransaction(any()) } returns Unit

        useCase(validTransaction)

        coVerify(exactly = 1) { repository.addTransaction(validTransaction) }
    }

    @Test
    fun `quand montant est zero, une exception est lancee`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase(validTransaction.copy(amount = 0.0))
            }
        }
        assertEquals(AddTransactionUseCase.INVALID_AMOUNT, exception.message)
    }

    @Test
    fun `quand montant est negatif, une exception est lancee`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase(validTransaction.copy(amount = -10.0))
            }
        }
        assertEquals(AddTransactionUseCase.INVALID_AMOUNT, exception.message)
    }

    @Test
    fun `quand description est vide, une exception est lancee`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase(validTransaction.copy(description = ""))
            }
        }
        assertEquals(AddTransactionUseCase.INVALID_DESCRIPTION, exception.message)
    }
}