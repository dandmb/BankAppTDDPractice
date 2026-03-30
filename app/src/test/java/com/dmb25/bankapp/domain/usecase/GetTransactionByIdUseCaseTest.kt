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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetTransactionByIdUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetTransactionByIdUseCase

    private val testCategory = Category(
        id = 1L,
        name = "Food",
        iconName = "restaurant",
        colorHex = "#FF5722"
    )

    private val testTransaction = Transaction(
        id = 1L,
        amount = 100.0,
        description = "Test",
        type = TransactionType.EXPENSE,
        category = testCategory,
        date = LocalDateTime.now(),
        accountId = 110L
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetTransactionByIdUseCase(repository)
    }

    @Test
    fun `quand id valide, la bonne transaction est retournee`() = runTest {
        // ARRANGE
        val slot = slot<Long>()
        coEvery { repository.getTransactionById(capture(slot)) } returns testTransaction

        // ACT
        val result = useCase(transactionId = 1L)

        // ASSERT
        assertEquals(1L, slot.captured)            // bon id passé au repo
        assertEquals(testTransaction, result)      // bonne transaction retournée
        assertNotNull(result)                      // pas null
    }

    @Test
    fun `quand id valide, le repository est appele exactement une fois`() = runTest {
        coEvery { repository.getTransactionById(any()) } returns testTransaction

        useCase(transactionId = 1L)

        coVerify(exactly = 1) { repository.getTransactionById(1L) }
    }

    @Test
    fun `quand transaction non trouvee, null est retourne`() = runTest {
        coEvery { repository.getTransactionById(any()) } returns null

        val result = useCase(transactionId = 99L)

        assertNull(result)
    }


    @Test
    fun `quand id est zero, une exception est lancee`() {
        // ACT + ASSERT
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { useCase(transactionId = 0L) }
        }
        assertEquals(GetTransactionByIdUseCase.INVALID_TRANSACTION_ID, exception.message)
    }

    @Test
    fun `quand id est zero, le repository n est jamais appele`() {
        runCatching { runBlocking { useCase(transactionId = 0L) } }
        coVerify(exactly = 0) { repository.getTransactionById(any()) }
    }
}