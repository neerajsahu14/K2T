package com.app.k2t.ui.presentation.screen.table.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.k2t.firebase.model.Order
import com.app.k2t.firebase.model.OrderItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ActiveOrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    items: List<OrderItem>
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val formattedDate = order.createdAt?.let {
                SimpleDateFormat("hh:mm a, dd/MM/yy", Locale.getDefault()).format(it)
            } ?: "N/A"

            Text(
                text = "Order at: $formattedDate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Total: ₹${String.format("%.2f", order.totalPrice)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Items",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (items.isNotEmpty()) {
                items.forEach { item ->
                    OrderedFoodCard(item = item)
                }
            } else {
                Text("Items are being processed...")
            }
        }
    }
}
