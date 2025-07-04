package com.app.k2t.ui.presentation.screen.table.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.k2t.firebase.model.OrderItem
import com.app.k2t.firebase.utils.OrderStatus
import java.util.Date

@Composable
fun OrderedFoodCard(
    modifier: Modifier = Modifier,
    item: OrderItem
) {
    val statusColor = when (item.statusCode) {
        OrderStatus.PENDING.code -> Color.Gray
        OrderStatus.PREPARING.code -> Color(0xFFFFA500) // Orange
        OrderStatus.COMPLETED.code -> Color.Green
        else -> Color.Black
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.foodName ?: "Unknown Item", fontWeight = FontWeight.Bold)
                Text(text = "Quantity: ${item.quantity}")
            }
            Text(
                text = OrderStatus.fromCode(item.statusCode ?: 0).displayName,
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(name = "OrderedFoodCard")
@Composable
private fun PreviewOrderedFoodCard() {
    OrderedFoodCard(
        item = OrderItem(
            foodName = "Preview Food",
            quantity = 2,
            statusCode = 1,
            tableId = "",
            addedAt = Date()
        )
    )
}
