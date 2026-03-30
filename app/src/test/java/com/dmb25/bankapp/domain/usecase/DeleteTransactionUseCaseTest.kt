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
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class DeleteTransactionUseCaseTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var  deleteTransactionUseCase : DeleteTransactionUseCase

    @Before
    fun setup() {
        transactionRepository = mockk()
        deleteTransactionUseCase = DeleteTransactionUseCase(transactionRepository)
    }

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


    @Test
    fun `should delete transaction if it exists`() = runTest{
        val transaction = Transaction(
            amount = 100.0,
            description = "Test",
            type = TransactionType.EXPENSE,
            category = testCategory,
            id = 1,
            date = LocalDateTime.now(),
            accountId = 110L,
        )
        coEvery{ transactionRepository.deleteTransaction(any())} returns Unit

        deleteTransactionUseCase(transaction)

        coVerify(exactly = 1) { transactionRepository.deleteTransaction(any()) }

    }

    @Test
    fun `should not call the repo if transaction does not exist`(){
        val transaction = Transaction(
            amount = 100.0,
            description = "Test",
            type = TransactionType.EXPENSE,
            category = testCategory,
            id = 0,
            date = LocalDateTime.now(),
            accountId = 110L,
        )
        coEvery{ transactionRepository.deleteTransaction(any())} returns Unit
        runCatching {
            runBlocking {
                deleteTransactionUseCase(transaction)
            }
        }
        coVerify(exactly = 0) { transactionRepository.deleteTransaction(any()) }

    }

    @Test
    fun `should delete only the transaction with the given id`(){
        val transaction = Transaction(
            amount = 100.0,
            description = "Test",
            type = TransactionType.EXPENSE,
            category = testCategory,
            id = 1,
            date = LocalDateTime.now(),
            accountId = 110L,
        )
        val slot = slot<Transaction>()

        coEvery{ transactionRepository.deleteTransaction(capture(slot))} returns Unit

        runBlocking {
            deleteTransactionUseCase(transaction)
        }
        assertEquals(transaction, slot.captured)
    }

    @Test
    fun `should throw exception if transaction id is 0`() {

        val transaction = Transaction(
            amount = 100.0,
            description = "Test",
            type = TransactionType.EXPENSE,
            category = testCategory,
            id = 0,
            date = LocalDateTime.now(),
            accountId = 110L,
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                deleteTransactionUseCase(transaction)
            }
        }
        assertEquals(DeleteTransactionUseCase.INVALID_TRANSACTION_ID, exception.message)
    }

}