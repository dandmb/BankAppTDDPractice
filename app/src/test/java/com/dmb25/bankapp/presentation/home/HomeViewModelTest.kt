package com.dmb25.bankapp.presentation.home

import com.dmb25.bankapp.domain.usecase.GetBalanceUseCase
import com.dmb25.bankapp.domain.usecase.GetTransactionsUseCase
import io.mockk.mockk
import org.junit.Before

import app.cash.turbine.test
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getBalanceUseCase: GetBalanceUseCase
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val category = Category(
        id = 1L,
        name = "Food",
        iconName = "restaurant",
        colorHex = "#FF5722"
    )

    private val transactions = listOf(
        Transaction(
            id = 1L,
            amount = 2500.0,
            description = "Salaire",
            type = TransactionType.INCOME,
            category = category,
            date = LocalDateTime.now(),
            accountId = 1L
        ),
        Transaction(
            id = 2L,
            amount = 100.0,
            description = "Courses",
            type = TransactionType.EXPENSE,
            category = category,
            date = LocalDateTime.now(),
            accountId = 1L
        )
    )

    @Before
    fun setup() {

        Dispatchers.setMain(testDispatcher)

        getBalanceUseCase = mockk()
        getTransactionsUseCase = mockk()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = HomeViewModel(
            getBalanceUseCase = getBalanceUseCase,
            getTransactionsUseCase = getTransactionsUseCase
        )
    }


    @Test
    fun whenViewModelCreated_initialStateIsLoading() = runTest {
        every { getBalanceUseCase(any()) } returns flowOf(0.0)
        every { getTransactionsUseCase(any()) } returns flowOf(emptyList())

        createViewModel()

        viewModel.uiState.test {
            assertTrue(awaitItem() is HomeUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun whenDataLoaded_stateIsSuccess() = runTest {
        every { getBalanceUseCase(any()) } returns flowOf(2400.0)
        every { getTransactionsUseCase(any()) } returns flowOf(transactions)

        createViewModel()

        viewModel.uiState.test {
            assertTrue(awaitItem() is HomeUiState.Loading)

            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem() as HomeUiState.Success
            assertEquals(2400.0, state.balance, 0.0)
            assertEquals(2, state.transactions.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenDataLoaded_balanceIsCorrect() = runTest {
        every { getBalanceUseCase(any()) } returns flowOf(2400.0)
        every { getTransactionsUseCase(any()) } returns flowOf(transactions)

        createViewModel()

        viewModel.uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem() as HomeUiState.Success
            assertEquals(2400.0, state.balance, 0.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenDataLoaded_transactionsAreCorrect() = runTest {
        every { getBalanceUseCase(any()) } returns flowOf(2400.0)
        every { getTransactionsUseCase(any()) } returns flowOf(transactions)

        createViewModel()

        viewModel.uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem() as HomeUiState.Success
            assertEquals(2, state.transactions.size)
            assertEquals("Salaire", state.transactions[0].description)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenNoTransactions_stateIsSuccessWithEmptyList() = runTest {
        every { getBalanceUseCase(any()) } returns flowOf(0.0)
        every { getTransactionsUseCase(any()) } returns flowOf(emptyList())

        createViewModel()

        viewModel.uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem() as HomeUiState.Success
            assertEquals(0.0, state.balance, 0.0)
            assertEquals(0, state.transactions.size)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun whenBalanceUseCaseFails_stateIsError() = runTest {
        every { getBalanceUseCase(any()) } returns kotlinx.coroutines.flow.flow {
            throw RuntimeException("Erreur réseau")
        }
        every { getTransactionsUseCase(any()) } returns flowOf(emptyList())

        createViewModel()

        viewModel.uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem() as HomeUiState.Error
            assertEquals("Erreur réseau", state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenTransactionsUseCaseFails_stateIsError() = runTest {
        every { getBalanceUseCase(any()) } returns flowOf(0.0)
        every { getTransactionsUseCase(any()) } returns kotlinx.coroutines.flow.flow {
            throw RuntimeException("DB error")
        }

        createViewModel()

        viewModel.uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem() as HomeUiState.Error
            assertEquals("DB error", state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}