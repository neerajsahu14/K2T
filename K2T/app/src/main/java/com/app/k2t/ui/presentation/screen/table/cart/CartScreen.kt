package com.app.k2t.ui.presentation.screen.table.cart

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
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
    contentPadding: PaddingValues = PaddingValues(),
    cartViewModel: CartViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onBrowseMenuClick: () -> Unit = {}
) {
    val cartItemsState = cartViewModel.allFoodInCart.collectAsState()
    val cartItems = cartItemsState.value
    val total = cartItems.sumOf { it.totalPrice ?: 0.0 }

    val orderPlacementStatus by orderViewModel.orderPlacementState.collectAsState()
    val context = LocalContext.current

    // User details - assuming tableId and tableNumber might come from user session
    val currentUser by userViewModel.userState.collectAsState()
    val tableId = currentUser?.tableId // Correctly get tableId from user state
    val tableNumber = currentUser?.tableNumber ?: "T1" // Replace with actual logic


    LaunchedEffect(orderPlacementStatus) {
        when (val status = orderPlacementStatus) {
            is OrderViewModel.OrderPlacementStatus.OrderPlaced -> {
                Toast.makeText(context, "Order placed successfully! ID: ${status.orderId}", Toast.LENGTH_LONG).show()
                orderViewModel.resetOrderPlacementStatus()
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
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add items from the menu to get started.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBrowseMenuClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Browse Menu")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = contentPadding.calculateTopPadding() + 8.dp,
                    bottom = contentPadding.calculateBottomPadding() + 8.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(cartItems.size) { index ->
                    val item = cartItems[index]
                    Box(modifier = Modifier.widthIn(max = 840.dp)) {
                        CartFoodCard(
                            cartFood = item,
                            onIncrease = {
                                val newQty = (item.quantity ?: 1) + 1
                                cartViewModel.updateFood(item.copy(quantity = newQty))
                            },
                            onDecrease = {
                                val newQty = (item.quantity ?: 1) - 1
                                if (newQty > 0) {
                                    cartViewModel.updateFood(item.copy(quantity = newQty))
                                }
                            },
                            onDelete = {
                                cartViewModel.deleteFood(item)
                            }
                        )
                    }
                }
            }

            // Bottom Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f))
                    .padding(bottom = contentPadding.calculateBottomPadding()),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: ₹${String.format(Locale.getDefault(), "%.2f", total)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedButton(
                            onClick = { cartViewModel.clearCart() },
                            enabled = orderPlacementStatus != OrderViewModel.OrderPlacementStatus.Processing,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = cartItems.isNotEmpty() && orderPlacementStatus != OrderViewModel.OrderPlacementStatus.Processing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (orderPlacementStatus == OrderViewModel.OrderPlacementStatus.Processing ||
                            orderPlacementStatus == OrderViewModel.OrderPlacementStatus.PaymentSuccess
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Place Order")
                        }
                    }
                }
            }
        }
    }
}
