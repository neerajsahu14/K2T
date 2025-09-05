package com.app.k2t.ui.presentation.screen.admin.order.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.k2t.firebase.model.OrderItem

@Composable
fun IncompleteOrders(modifier: Modifier = Modifier, orders: List<OrderItem>) {
    val sortedOrders = orders.sortedBy { it.statusCode }
    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        sortedOrders.forEach { order ->
            Card(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(text = "Order: ${order.itemId}, Status: ${order.statusCode}", modifier = Modifier.padding(16.dp))
            }
        }
    }
}