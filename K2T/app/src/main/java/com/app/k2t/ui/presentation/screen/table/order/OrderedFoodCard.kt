package com.app.k2t.ui.presentation.screen.table.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.material3.MaterialTheme

@Composable
fun OrderedFoodCard(
    modifier: Modifier = Modifier,
    item: OrderItem
) {
    val statusColor = when (item.statusCode) {
        OrderStatus.PENDING.code -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        OrderStatus.PREPARING.code -> MaterialTheme.colorScheme.secondary
        OrderStatus.COMPLETED.code -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    ListItem(
        headlineContent = { Text(text = item.foodName ?: "Unknown Item", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        supportingContent = { Text(text = "Quantity: ${item.quantity}", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = {
            Text(
                text = OrderStatus.fromCode(item.statusCode ?: 0).displayName,
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
    )
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
