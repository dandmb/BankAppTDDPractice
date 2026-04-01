package com.dmb25.bankapp.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dmb25.bankapp.domain.model.Category
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.presentation.home.ErrorContent
import com.dmb25.bankapp.presentation.home.HomeUiState
import com.dmb25.bankapp.presentation.home.LoadingContent
import com.dmb25.bankapp.presentation.home.SuccessContent
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
            description = "Supermarché",
            type = TransactionType.EXPENSE,
            category = category,
            date = LocalDateTime.now(),
            accountId = 1L
        )
    )


    @Composable
    private fun HomeScreenWithState(state: HomeUiState) {

        when (state) {
            is HomeUiState.Loading -> LoadingContent()
            is HomeUiState.Success -> SuccessContent(
                state = state,
                onAddTransaction = {}
            )
            is HomeUiState.Error -> ErrorContent(
                message = state.message
            )
        }
    }


    @Test
    fun whenStateIsLoading_balanceIsNotDisplayed() {
        composeTestRule.setContent {
            HomeScreenWithState(state = HomeUiState.Loading)
        }

        composeTestRule
            .onNodeWithText("Solde total")
            .assertDoesNotExist()
    }


    @Test
    fun whenStateIsSuccess_balanceIsDisplayed() {
        composeTestRule.setContent {
            HomeScreenWithState(
                state = HomeUiState.Success(
                    balance = 2400.0,
                    transactions = transactions
                )
            )
        }

        composeTestRule
            .onNodeWithText("Solde total")
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsSuccess_transactionsAreDisplayed() {
        composeTestRule.setContent {
            HomeScreenWithState(
                state = HomeUiState.Success(
                    balance = 2400.0,
                    transactions = transactions
                )
            )
        }

        composeTestRule.onNodeWithText("Salaire").assertIsDisplayed()
        composeTestRule.onNodeWithText("Supermarché").assertIsDisplayed()
    }

    @Test
    fun whenStateIsSuccess_addButtonIsDisplayed() {
        composeTestRule.setContent {
            HomeScreenWithState(
                state = HomeUiState.Success(
                    balance = 2400.0,
                    transactions = transactions
                )
            )
        }

        composeTestRule
            .onNodeWithText("+ Ajouter une transaction")
            .assertIsDisplayed()
    }

    @Test
    fun whenStateIsSuccess_emptyList_noTransactionsShown() {
        composeTestRule.setContent {
            HomeScreenWithState(
                state = HomeUiState.Success(
                    balance = 0.0,
                    transactions = emptyList()
                )
            )
        }

        composeTestRule.onNodeWithText("Salaire").assertDoesNotExist()
    }

    // =========================================
    // Error
    // =========================================

    @Test
    fun whenStateIsError_errorMessageIsDisplayed() {
        composeTestRule.setContent {
            HomeScreenWithState(
                state = HomeUiState.Error(
                    message = "Une erreur est survenue"
                )
            )
        }

        composeTestRule
            .onNodeWithText("Une erreur est survenue")
            .assertIsDisplayed()
    }
}