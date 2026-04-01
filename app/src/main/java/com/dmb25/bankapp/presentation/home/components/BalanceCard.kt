package com.dmb25.bankapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmb25.bankapp.presentation.home.BankBlue

@Composable
fun BalanceCard(
    balance: Double,
    totalIncome: Double,
    totalExpense: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BankBlue),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Solde total",
                color = Color(0xFFB5D4F4),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%.2f €".format(balance),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BalanceStat(
                    label = "Revenus",
                    amount = "+ %.0f €".format(totalIncome),
                    modifier = Modifier.weight(1f)
                )
                BalanceStat(
                    label = "Dépenses",
                    amount = "- %.0f €".format(totalExpense),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BalanceStat(
    label: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = label, color = Color(0xFFB5D4F4), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = amount, color = Color.White, fontSize = 14.sp,
                fontWeight = FontWeight.Medium)
        }
    }
}
