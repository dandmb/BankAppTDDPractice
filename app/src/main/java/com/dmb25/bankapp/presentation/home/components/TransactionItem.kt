package com.dmb25.bankapp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmb25.bankapp.domain.model.Transaction
import com.dmb25.bankapp.domain.model.TransactionType
import com.dmb25.bankapp.presentation.home.ExpenseRed
import com.dmb25.bankapp.presentation.home.IncomeGreen
import java.time.format.DateTimeFormatter


@Composable
fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val amountText = if (isIncome) {
        "+ %.0f €".format(transaction.amount)
    } else {
        "- %.0f €".format(transaction.amount)
    }
    val bgColor = if (isIncome) Color(0xFFEAF3DE) else Color(0xFFFAECE7)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icône catégorie
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isIncome) "↑" else "↓",
                    color = amountColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Description + date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                transaction.date?.let {
                    Text(
                        text = it.format(
                            DateTimeFormatter.ofPattern("dd MMM yyyy")
                        ),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Montant
            Text(
                text = amountText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = amountColor
            )
        }
    }
}