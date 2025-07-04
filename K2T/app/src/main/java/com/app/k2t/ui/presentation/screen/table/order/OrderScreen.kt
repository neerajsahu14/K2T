package com.app.k2t.ui.presentation.screen.table.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.k2t.R
import com.app.k2t.ui.presentation.viewmodel.OrderItemViewModel
import com.app.k2t.ui.presentation.viewmodel.OrderViewModel
import com.app.k2t.ui.presentation.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel = koinViewModel(),
    orderItemViewModel: OrderItemViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val user by userViewModel.userState.collectAsState()
    val activeOrders by orderViewModel.activeOrdersForTable.collectAsState()
    val allOrderItems by orderItemViewModel.allOrderItems.collectAsState()

    LaunchedEffect(user?.tableId) {
        user?.tableId?.let {
            orderViewModel.getActiveOrdersForTable(it)
        }
    }
    val tableNumber = user?.tableNumber ?: "T1"

    if (activeOrders.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(R.drawable.baseline_receipt_24),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Orders",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Orders for $tableNumber will appear here",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(activeOrders) { order ->
                val itemsForThisOrder = allOrderItems.filter { it.orderId == order.orderId }
                ActiveOrderCard(order = order, items = itemsForThisOrder)
            }
        }
    }
}

@Preview(name = "OrderScreen")
@Composable
private fun PreviewOrderScreen() {
    OrdersScreen()
}
