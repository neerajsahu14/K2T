package com.app.k2t.ui.presentation.screen.table.cart

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.k2t.ui.presentation.viewmodel.CartViewModel
import com.app.k2t.ui.presentation.viewmodel.OrderViewModel
import com.app.k2t.ui.presentation.viewmodel.UserViewModel
import java.util.Locale

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    cartViewModel: CartViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel() // Assuming you might need user details like tableId
    // orderItemViewModel: OrderItemViewModel = viewModel() // Not directly used in UI logic here
) {
    val cartItemsState = cartViewModel.allFoodInCart.collectAsState()
    val cartItems = cartItemsState.value
    val total = cartItems.sumOf { it.totalPrice ?: 0.0 }

    val orderPlacementStatus by orderViewModel.orderPlacementState.collectAsState()
    val context = LocalContext.current

    // User details - assuming tableId and tableNumber might come from user session
    // For this example, let's assume they are available or can be hardcoded/fetched
    val currentUser by userViewModel.userState.collectAsState()
    val tableId = currentUser?.tableId // Correctly get tableId from user state
    val tableNumber = currentUser?.tableNumber ?: "T1" // Replace with actual logic


    LaunchedEffect(orderPlacementStatus) {
        when (val status = orderPlacementStatus) {
            is OrderViewModel.OrderPlacementStatus.OrderPlaced -> {
                Toast.makeText(context, "Order placed successfully! ID: ${status.orderId}", Toast.LENGTH_LONG).show()
                // Optionally navigate to an order confirmation screen or back
                // navController.navigate("orderConfirmation/${statusCode.orderId}")
                orderViewModel.resetOrderPlacementStatus() // Reset statusCode after handling
            }
            is OrderViewModel.OrderPlacementStatus.Error -> {
                Toast.makeText(context, "Error: ${status.message}", Toast.LENGTH_LONG).show()
                orderViewModel.resetOrderPlacementStatus()
            }
            is OrderViewModel.OrderPlacementStatus.PaymentSuccess -> {
                Toast.makeText(context, "Payment successful, placing order...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Idle or Processing
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(cartItems.size) { index ->
                    val item = cartItems[index]
                    CartItemCard(
                        cartFood = item,
                        onIncrease = {
                            val newQty = (item.quantity ?: 1) + 1
                            cartViewModel.updateFood(item.copy(
                                quantity = newQty,
                                totalPrice = (item.unitPrice ?: 0.0) * newQty
                            ))
                        },
                        onDecrease = {
                            val newQty = (item.quantity ?: 1) - 1
                            if (newQty > 0) {
                                cartViewModel.updateFood(item.copy(
                                    quantity = newQty,
                                    totalPrice = (item.unitPrice ?: 0.0) * newQty
                                ))
                            } else {
                                // Optionally delete if quantity becomes 0, or disable decrease button
                                cartViewModel.deleteFood(item)
                            }
                        },
                        onDelete = {
                            cartViewModel.deleteFood(item)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: ₹${String.format(Locale.getDefault(), "%.2f", total)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = { cartViewModel.clearCart() },
                        enabled = orderPlacementStatus != OrderViewModel.OrderPlacementStatus.Processing
                    ) {
                        Text("Clear Cart")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        orderViewModel.placeOrder(
                            tableId = tableId.toString(), // Pass actual tableId
                            tableNumber = tableNumber // Pass actual tableNumber
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cartItems.isNotEmpty() && orderPlacementStatus != OrderViewModel.OrderPlacementStatus.Processing
                ) {
                    if (orderPlacementStatus == OrderViewModel.OrderPlacementStatus.Processing ||
                        orderPlacementStatus == OrderViewModel.OrderPlacementStatus.PaymentSuccess) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...")
                    } else {
                        Text("Place Order")
                    }
                }
            }
        }
    }
}


