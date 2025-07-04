package com.app.k2t.ui.presentation.screen.chef.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.k2t.firebase.utils.OrderStatus
import com.app.k2t.ui.presentation.viewmodel.OrderItemViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AcceptedOrderScreen(
    modifier: Modifier = Modifier,
    orderItemViewModel: OrderItemViewModel = koinViewModel()
) {
    val acceptedItems by orderItemViewModel.acceptedOrderItems.collectAsState()

    if (acceptedItems.isEmpty()) {
        Column(modifier = modifier.fillMaxSize()) {
            Text(text = "No accepted food items.")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(acceptedItems) { item ->
                item.itemId?.let { itemId ->
                    AcceptedOrderCart(
                        item = item,
                        onItemCompleted = {
                            orderItemViewModel.updateOrderItemStatus(itemId, OrderStatus.COMPLETED.code)
                        }
                    )
                }
            }
        }
    }
}
