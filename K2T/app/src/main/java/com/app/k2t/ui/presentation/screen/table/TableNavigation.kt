package com.app.k2t.ui.presentation.screen.table

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.k2t.firebase.model.Food
import com.app.k2t.ui.presentation.screen.table.home.*
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import com.app.k2t.R
import com.app.k2t.ui.presentation.screen.table.cart.CartScreen
import com.app.k2t.ui.presentation.screen.table.order.OrdersScreen

// AdminNavigation routes
sealed class TableRoute(val route: String) {
    object Menu : TableRoute("menu")
    object FoodDetails : TableRoute("food_details")
    object Cart : TableRoute("cart")
    object Orders : TableRoute("orders")
    object Profile : TableRoute("profile")
}

// Cart item data class
data class CartItem(
    val food: Food,
    val quantity: Int
)

data class NavigationItem(
    val route: String,
    val icon: Int,
    val label: String,
    val badgeCount: Int?
)



//@Composable
//fun OrdersScreen(
//    tableNumber: String,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Icon(
//            painterResource(R.drawable.baseline_receipt_24),
//            contentDescription = null,
//            modifier = Modifier.size(80.dp),
//            tint = MaterialTheme.colorScheme.primary
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = "Your Orders",
//            style = MaterialTheme.typography.headlineLarge,
//            fontWeight = FontWeight.Bold
//        )
//        Text(
//            text = "Orders for $tableNumber will appear here",
//            style = MaterialTheme.typography.bodyLarge,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            textAlign = TextAlign.Center
//        )
//    }
//}

@Composable
fun ProfileScreen(
    tableNumber: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(R.drawable.baseline_table_restaurant_24),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = tableNumber,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Welcome to K2T Restaurant",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Service options
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { /* Call waiter */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call Waiter")
            }

            OutlinedButton(
                onClick = { /* Request bill */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.baseline_receipt_24), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Request Bill")
            }

            OutlinedButton(
                onClick = { /* Feedback */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.baseline_feedback_24), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Feedback")
            }
        }
    }
}

@Preview(name = "TableNavigation")
@Composable
private fun PreviewCustomerTableNavigation() {
    MaterialTheme {
        TableNavigation()
    }
}

@Preview(name = "TableNavigation", device = Devices.PIXEL_TABLET)
@Composable
private fun PreviewCustomerTableNavigationTablet() {
    MaterialTheme {
        TableNavigation()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableNavigation(
    modifier: Modifier = Modifier,
    foodViewModel: FoodViewModel = viewModel(),
    ) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf(TableRoute.Menu.route) }
    var selectedFood by remember { mutableStateOf<Food?>(null) }
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }

    val cartItemCount = cartItems.sumOf { it.quantity }
    val cartTotal = cartItems.sumOf { (it.food.price ?: 0.0) * it.quantity }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navigationItems = listOf(
                    NavigationItem(
                        route = TableRoute.Menu.route,
                        icon = R.drawable.restaurant,
                        label = "Menu",
                        badgeCount = null
                    ),
                    NavigationItem(
                        route = TableRoute.Cart.route,
                        icon = R.drawable.baseline_shopping_cart_24,
                        label = "Cart",
                        badgeCount = if (cartItemCount > 0) cartItemCount else null
                    ),
                    NavigationItem(
                        route = TableRoute.Orders.route,
                        icon = R.drawable.baseline_receipt_24,
                        label = "Orders",
                        badgeCount = null
                    ),
                    NavigationItem(
                        route = TableRoute.Profile.route,
                        icon = R.drawable.baseline_person_24,
                        label = "Profile",
                        badgeCount = null
                    )
                )

                navigationItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            BadgedBox(
                                badge = {
                                    item.badgeCount?.let { count ->
                                        Badge { Text(count.toString()) }
                                    }
                                }
                            ) {
                                Icon(painterResource(id = item.icon), contentDescription = item.label)
                            }
                        },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            currentRoute = item.route
                            navController.navigate(item.route) {
                                popUpTo(TableRoute.Menu.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = TableRoute.Menu.route,
            modifier = Modifier.padding(paddingValues) // No padding here
        ) {
            composable(TableRoute.Menu.route) {
                TableHomeScreen(
                    foodViewModel = foodViewModel,
                    onFoodClick = { food ->
                        selectedFood = food
                        currentRoute = TableRoute.FoodDetails.route
                        navController.navigate(TableRoute.FoodDetails.route)
                    },
                    onCartClick = {
                        currentRoute = TableRoute.Cart.route
                        navController.navigate(TableRoute.Cart.route)
                    },
                )
            }

            composable(TableRoute.FoodDetails.route) {
                selectedFood?.let { food ->
                    TableFoodDetailsScreen(
                        food = food,
                        onBackClick = {
                            navController.navigateUp()
                            currentRoute = TableRoute.Menu.route
                        },
                        onAddToCart = { quantity ->
                            // Add to cart logic
                            val existingItemIndex = cartItems.indexOfFirst { it.food.foodId == food.foodId }
                            cartItems = if (existingItemIndex >= 0) {
                                cartItems.toMutableList().apply {
                                    this[existingItemIndex] = this[existingItemIndex].copy(
                                        quantity = this[existingItemIndex].quantity + quantity
                                    )
                                }
                            } else {
                                cartItems + CartItem(food, quantity)
                            }

                            // Navigate to cart
                            currentRoute = TableRoute.Cart.route
                            navController.navigate(TableRoute.Cart.route)
                        }
                    )
                }
            }

            composable(TableRoute.Cart.route) {
                CartScreen()
            }

            composable(TableRoute.Orders.route) {
                OrdersScreen()
            }

            composable(TableRoute.Profile.route) {
                ProfileScreen(
                    tableNumber = "tableNumber"
                )
            }
        }
    }
}