package com.app.k2t.ui.presentation.screen.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.k2t.firebase.model.Food
import com.app.k2t.ui.presentation.screen.table.home.*
import com.app.k2t.R
import com.app.k2t.ui.presentation.screen.table.cart.CartScreen
import com.app.k2t.ui.presentation.screen.table.order.OrdersScreen
import com.app.k2t.ui.presentation.viewmodel.CartViewModel
import com.app.k2t.ui.theme.K2TTheme
import org.koin.androidx.compose.koinViewModel

// TableNavigation routes
sealed class TableRoute(val route: String) {
    object Menu : TableRoute("menu")
    object FoodDetails : TableRoute("food_details")
    object Cart : TableRoute("cart")
    object Orders : TableRoute("orders")
    object Profile : TableRoute("profile")
    object CategoryScreen : TableRoute("category_screen")

    object CategoryFoodScreen : TableRoute("category_food_screen")
}


data class NavigationItem(
    val route: String,
    val icon: Int,
    val label: String,
    val badgeCount: Int?
)


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
        Surface(
            modifier = Modifier.size(120.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp
        ) {
            Icon(
                painterResource(R.drawable.baseline_table_restaurant_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(24.dp)
                    .size(80.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = tableNumber,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Welcome to K2T Restaurant",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Service options with improved buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            FilledTonalButton(
                onClick = { /* Call waiter */ },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Call Waiter", style = MaterialTheme.typography.titleMedium)
            }

            FilledTonalButton(
                onClick = { /* Request bill */ },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(painterResource(R.drawable.baseline_receipt_24), contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Request Bill", style = MaterialTheme.typography.titleMedium)
            }

            FilledTonalButton(
                onClick = { /* Feedback */ },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(painterResource(R.drawable.baseline_feedback_24), contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Give Feedback", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(tableNumber = "Table T1")
}

// ...existing code...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableNavigation(
    modifier: Modifier = Modifier,
    cartViewModel: CartViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var selectedFood by remember { mutableStateOf<Food?>(null) }
    val cartItems by cartViewModel.allFoodInCart.collectAsState()

    val cartItemCount = cartItems.sumOf { it.quantity ?: 0 }


    // Show bottom bar only on main screens, not on detail screens
    val showBottomBar = remember(currentDestination) {
        currentDestination?.route != TableRoute.FoodDetails.route
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp,
                ) {
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
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        val scale = if (selected) 1.15f else 1.0f
                        NavigationBarItem(
                            icon = {
                                BadgedBox(
                                    badge = {
                                        item.badgeCount?.let { count ->
                                            Badge {
                                                Text(count.toString())
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        painterResource(id = item.icon),
                                        contentDescription = item.label,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .graphicsLayer {
                                                scaleX = scale
                                                scaleY = scale
                                            }
                                    )
                                }
                            },
                            label = {
                                Text(
                                    item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.graphicsLayer {
                                        alpha = if (selected) 1f else 0.7f
                                    }
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = cartItems.isNotEmpty() && navController.currentDestination?.route != TableRoute.Cart.route,
                enter = fadeIn() + scaleIn(initialScale = 0.5f),
                exit = fadeOut() + scaleOut(targetScale = 0.5f),
                modifier = Modifier
            ) {
                FloatingActionButton(
                        onClick = {
                            navController.navigate(TableRoute.Cart.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when navigating back to a previously selected item
                                restoreState = true
                            }
                        },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Cart"
                    )
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.offset(x = 8.dp, y = (-8).dp)
                    ) {
                        Text(
                            text = cartItems.sumOf { it.quantity ?: 0 }.toString(),
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = TableRoute.Menu.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(
                TableRoute.Menu.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                TableHomeScreen(
                    onFoodClick = { food ->
                        selectedFood = food
                        navController.navigate(TableRoute.FoodDetails.route)
                    },
                    onCartClick = {
                        navController.navigate(TableRoute.Cart.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when navigating back to a previously selected item
                            restoreState = true
                        }
                    },
                    onCategoryClick = {
                        navController.navigate(TableRoute.CategoryScreen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when navigating back to a previously selected item
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                TableRoute.FoodDetails.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                selectedFood?.let { food ->
                    TableFoodDetailsScreen(
                        food = food,
                        onBackClick = {
                            navController.navigateUp()
                        }
                    )
                }
            }

            composable(
                TableRoute.Cart.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                CartScreen(
                    onBrowseMenuClick = {
                        navController.navigate(TableRoute.Menu.route){
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    }
                )
            }

            composable(
                TableRoute.Orders.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                OrdersScreen()
            }

            composable(
                TableRoute.Profile.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(300))
                }
            ) {
                ProfileScreen(
                    tableNumber = "Table T1"
                )
            }

            composable(TableRoute.CategoryScreen.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    }
                ) {
                CategoryScreen(
                    onBackClick = {
                        navController.navigateUp()
                    },
                    onCategoryClick = { categoryId ->

                        navController.navigate("CategoryFoodScreen/$categoryId")
                    },
                )

            }
            composable(
                route = "CategoryFoodScreen/{categoryId}",
                arguments = listOf(
                    navArgument("categoryId") { nullable = false }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                CategoryFoodScreen(
                    categoryId = categoryId,
                    onBackClick = { navController.popBackStack() },
                    onFoodClick = { food ->
                        selectedFood = food
                        navController.navigate(TableRoute.FoodDetails.route)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun TableNavigationPreview() {
    K2TTheme { TableNavigation() }
}