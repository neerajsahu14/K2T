package com.app.k2t.ui.presentation.screen.admin.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.k2t.ui.presentation.screen.admin.order.cards.CompletedOrder
import com.app.k2t.ui.presentation.screen.admin.order.cards.IncompleteOrders
import com.app.k2t.firebase.model.Order
import com.app.k2t.firebase.model.OrderItem
import java.util.Date
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.k2t.ui.presentation.viewmodel.OrderItemViewModel
import com.app.k2t.ui.presentation.viewmodel.OrderViewModel

@Composable
fun OrdersScreen(
    orderViewModel: OrderViewModel = viewModel(),
    orderItemViewModel: OrderItemViewModel = viewModel()
) {
    val orders = orderViewModel.activeOrdersForTable.collectAsStateWithLifecycle(emptyList()).value
    val orderItems = orderItemViewModel.allOrderItems.collectAsStateWithLifecycle(emptyList()).value

    val selectedDate = remember { mutableStateOf<Date?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = "Orders", modifier = Modifier.padding(bottom = 16.dp))

        // Calendar placeholder
        Button(onClick = { /* Show calendar dialog to select date */ }) {
            Text(text = selectedDate.value?.toString() ?: "Select Date")
        }

        val filteredOrders = orders.filter { order ->
            selectedDate.value?.let { date ->
                // Filter logic based on selected date
                order.createdAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate() ==
                        date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            } ?: true
        }

        val filteredOrderItems = orderItems.filter { orderItem ->
            selectedDate.value?.let { date ->
                orderItem.addedAt?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate() ==
                        date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            } ?: true
        }

        Text(text = "Incomplete Orders", modifier = Modifier.padding(bottom = 8.dp))
        IncompleteOrders(modifier = Modifier.padding(bottom = 16.dp), orders = filteredOrderItems)

        Text(text = "Completed Orders", modifier = Modifier.padding(bottom = 8.dp))
        CompletedOrder(modifier = Modifier.padding(bottom = 16.dp), orders = filteredOrders)
    }
}