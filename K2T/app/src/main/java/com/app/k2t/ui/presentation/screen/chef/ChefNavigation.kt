package com.app.k2t.ui.presentation.screen.chef

import AcceptedOrderScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.k2t.R
import com.app.k2t.ui.presentation.screen.chef.food.FoodStatusScreen
import com.app.k2t.ui.presentation.screen.chef.home.HomeScreen

sealed class ChefBottomNavItem(val route: String, val label: String, val screenName : String, val icon: Int) {
    object Incoming : ChefBottomNavItem("IncomingOrders", "Incoming",
        icon =R.drawable.baseline_receipt_24, screenName = "Incoming Orders"
    )
    object Accepted : ChefBottomNavItem("AcceptedOrders", "Accepted", "Accepted Orders",  R.drawable.check_circle)
    object FoodStatus : ChefBottomNavItem("FoodStatus", "Food Status", "Food Status",R.drawable.baseline_fastfood_24 )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChefNavigation(
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val bottomNavItems = listOf(ChefBottomNavItem.Incoming, ChefBottomNavItem.Accepted,
        ChefBottomNavItem.FoodStatus)

    Scaffold(
        modifier = modifier,
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val currentScreen = bottomNavItems.find { it.route == currentRoute }
            TopAppBar(
                title = { Text(text = currentScreen?.screenName ?: "Chef") }
            )
        },
        bottomBar = {
            NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(screen.icon), contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ChefBottomNavItem.Incoming.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ChefBottomNavItem.Incoming.route) {
                HomeScreen()
            }
            composable(ChefBottomNavItem.Accepted.route) {
                AcceptedOrderScreen()
            }
            composable ( ChefBottomNavItem.FoodStatus.route ){
                FoodStatusScreen()
            }
        }
    }
}
