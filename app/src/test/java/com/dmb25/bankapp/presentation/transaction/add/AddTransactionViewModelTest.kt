package com.dmb25.bankapp.presentation.transaction.add

import com.dmb25.bankapp.domain.usecase.AddTransactionUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.TransactionType
import io.mockk.coVerify
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModelTest {

    private lateinit var addTransactionUseCase: AddTransactionUseCase
    private lateinit var viewModel: AddTransactionViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val testCategory = Category(
        id = 1L,
        name = "Food",
        iconName = "restaurant",
        colorHex = "#FF5722"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        addTransactionUseCase = mockk()
        viewModel = AddTransactionViewModel(addTransactionUseCase)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenViewModelCreated_initialStateIsIdle() {
        assertTrue(viewModel.uiState.value is AddTransactionUiState.Idle)
    }

    @Test
    fun whenViewModelCreated_amountIsEmpty() {
        assertEquals("", viewModel.amount.value)
    }

    @Test
    fun whenViewModelCreated_descriptionIsEmpty() {
        assertEquals("", viewModel.description.value)
    }

    @Test
    fun whenViewModelCreated_typeIsExpense() {
        assertEquals(TransactionType.EXPENSE, viewModel.type.value)
    }

    @Test
    fun whenViewModelCreated_categoryIsNull() {
        assertNull(viewModel.selectedCategory.value)
    }

    @Test
    fun whenViewModelCreated_amountErrorIsNull() {
        assertNull(viewModel.amountError.value)
    }

    @Test
    fun whenViewModelCreated_descriptionErrorIsNull() {
        assertNull(viewModel.descriptionError.value)
    }

    @Test
    fun whenAmountChanged_amountIsUpdated() {
        viewModel.onAmountChange("100")
        assertEquals("100", viewModel.amount.value)
    }

    @Test
    fun whenValidAmountEntered_amountErrorIsNull() {
        viewModel.onAmountChange("100")
        assertNull(viewModel.amountError.value)
    }

    @Test
    fun whenZeroAmountEntered_amountErrorIsSet() {
        viewModel.onAmountChange("0")
        assertNotNull(viewModel.amountError.value)
    }

    @Test
    fun whenNegativeAmountEntered_amountErrorIsSet() {
        viewModel.onAmountChange("-10")
        assertNotNull(viewModel.amountError.value)
    }

    @Test
    fun whenInvalidTextEntered_amountErrorIsSet() {
        viewModel.onAmountChange("abc")
        assertNotNull(viewModel.amountError.value)
    }

    @Test
    fun whenAmountClearedAfterValid_amountErrorIsSet() {
        viewModel.onAmountChange("100")
        viewModel.onAmountChange("")
        assertNotNull(viewModel.amountError.value)
    }

    @Test
    fun whenDescriptionChanged_descriptionIsUpdated() {
        viewModel.onDescriptionChange("Supermarché")
        assertEquals("Supermarché", viewModel.description.value)
    }

    @Test
    fun whenValidDescriptionEntered_descriptionErrorIsNull() {
        viewModel.onDescriptionChange("Supermarché")
        assertNull(viewModel.descriptionError.value)
    }

    @Test
    fun whenEmptyDescriptionEntered_descriptionErrorIsSet() {
        viewModel.onDescriptionChange("")
        assertNotNull(viewModel.descriptionError.value)
    }

    @Test
    fun whenBlankDescriptionEntered_descriptionErrorIsSet() {
        viewModel.onDescriptionChange("   ")
        assertNotNull(viewModel.descriptionError.value)
    }

    @Test
    fun whenTypeChangedToIncome_typeIsUpdated() {
        viewModel.onTypeChange(TransactionType.INCOME)
        assertEquals(TransactionType.INCOME, viewModel.type.value)
    }

    @Test
    fun whenTypeChangedToExpense_typeIsUpdated() {
        viewModel.onTypeChange(TransactionType.INCOME)
        viewModel.onTypeChange(TransactionType.EXPENSE)
        assertEquals(TransactionType.EXPENSE, viewModel.type.value)
    }


    @Test
    fun whenCategorySelected_categoryIsUpdated() {
        viewModel.onCategoryChange(testCategory)
        assertEquals(testCategory, viewModel.selectedCategory.value)
    }

    @Test
    fun whenCategoryChanged_newCategoryIsSelected() {
        val otherCategory = testCategory.copy(id = 2L, name = "Transport")
        viewModel.onCategoryChange(testCategory)
        viewModel.onCategoryChange(otherCategory)
        assertEquals(otherCategory, viewModel.selectedCategory.value)
    }

    @Test
    fun whenFormIsValid_isFormValidIsTrue() {
        viewModel.onAmountChange("100")
        viewModel.onDescriptionChange("Supermarché")
        viewModel.onCategoryChange(testCategory)
        assertTrue(viewModel.isFormValid.value)
    }

    @Test
    fun whenAmountIsEmpty_isFormValidIsFalse() {
        viewModel.onAmountChange("")
        viewModel.onDescriptionChange("Supermarché")
        viewModel.onCategoryChange(testCategory)
        assertTrue(!viewModel.isFormValid.value)
    }

    @Test
    fun whenDescriptionIsEmpty_isFormValidIsFalse() {
        viewModel.onAmountChange("100")
        viewModel.onDescriptionChange("")
        viewModel.onCategoryChange(testCategory)
        assertTrue(!viewModel.isFormValid.value)
    }

    @Test
    fun whenCategoryIsNull_isFormValidIsFalse() {
        viewModel.onAmountChange("100")
        viewModel.onDescriptionChange("Supermarché")
        assertTrue(!viewModel.isFormValid.value)
    }

    @Test
    fun whenSaveTransaction_stateIsLoadingThenSuccess() = runTest {
        viewModel.onAmountChange("100")
        viewModel.onDescriptionChange("Supermarché")
        viewModel.onCategoryChange(testCategory)
        viewModel.onTypeChange(TransactionType.EXPENSE)
        coEvery { addTransactionUseCase(any()) } returns Unit

        viewModel.saveTransaction()

        assertTrue(viewModel.uiState.value is AddTransactionUiState.Loading)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AddTransactionUiState.Success)
    }

    @Test
    fun whenSaveTransaction_useCaseIsCalledOnce() = runTest {
        viewModel.onAmountChange("100")
        viewModel.onDescriptionChange("Supermarché")
        viewModel.onCategoryChange(testCategory)
        coEvery { addTransactionUseCase(any()) } returns Unit

        viewModel.saveTransaction()
        advanceUntilIdle()

        coVerify(exactly = 1) { addTransactionUseCase(any()) }
    }

    @Test
    fun whenSaveTransactionWithInvalidForm_useCaseIsNeverCalled() = runTest {
        viewModel.onAmountChange("")
        viewModel.onDescriptionChange("")

        viewModel.saveTransaction()
        advanceUntilIdle()

        coVerify(exactly = 0) { addTransactionUseCase(any()) }
    }

    @Test
    fun whenSaveTransactionWithInvalidForm_errorsAreDisplayed() = runTest {
        viewModel.onAmountChange("")
        viewModel.onDescriptionChange("")

        viewModel.saveTransaction()

        assertNotNull(viewModel.amountError.value)
        assertNotNull(viewModel.descriptionError.value)
    }


    @Test
    fun whenUseCaseFails_stateIsError() = runTest {
        viewModel.onAmountChange("100")
        viewModel.onDescriptionChange("Supermarché")
        viewModel.onCategoryChange(testCategory)
        coEvery { addTransactionUseCase(any()) } throws RuntimeException("DB error")

        viewModel.saveTransaction()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is AddTransactionUiState.Error)
        assertEquals("DB error", (state as AddTransactionUiState.Error).message)
    }

    @Test
    fun whenRetryAfterError_stateGoesBackToLoading() = runTest {
        viewModel.onAmountChange("100")
        viewModel.onDescriptionChange("Supermarché")
        viewModel.onCategoryChange(testCategory)
        coEvery { addTransactionUseCase(any()) } throws RuntimeException("DB error")
        viewModel.saveTransaction()
        advanceUntilIdle()

        coEvery { addTransactionUseCase(any()) } returns Unit
        viewModel.saveTransaction()

        assertTrue(viewModel.uiState.value is AddTransactionUiState.Loading)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AddTransactionUiState.Success)
    }
}