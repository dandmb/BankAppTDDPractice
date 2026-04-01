package com.dmb25.bankapp.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.presentation.home.components.BalanceCard
import com.dmb25.bankapp.presentation.home.components.TransactionItem
import org.koin.androidx.compose.koinViewModel

val BankBlue = Color(0xFF0C447C)
val IncomeGreen = Color(0xFF3B6D11)
val ExpenseRed = Color(0xFF993C1D)

@Composable
fun HomeScreen(
    onAddTransaction: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingContent(
                modifier = Modifier.padding(paddingValues)
            )
            is HomeUiState.Success -> SuccessContent(
                state = state,
                onAddTransaction = onAddTransaction,
                modifier = Modifier.padding(paddingValues)
            )
            is HomeUiState.Error -> ErrorContent(
                message = state.message,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
 fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = BankBlue)
    }
}

@Composable
fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun SuccessContent(
    state: HomeUiState.Success,
    onAddTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BalanceCard(
                balance = state.balance,
                totalIncome = state.transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount },
                totalExpense = state.transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
            )
        }

        item {
            Text(
                text = "Transactions récentes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        items(state.transactions) { transaction ->
            TransactionItem(transaction = transaction)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddTransaction,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BankBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+ Ajouter une transaction",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}


