package com.app.k2t.ui.presentation.screen.chef

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.k2t.ui.presentation.screen.chef.home.HomeScreen
import com.app.k2t.ui.presentation.screen.chef.order.AcceptedOrderScreen

sealed class ChefBottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Incoming : ChefBottomNavItem("IncomingOrders", "Incoming",
        Icons.AutoMirrored.Filled.List
    )
    object Accepted : ChefBottomNavItem("AcceptedOrders", "Accepted", Icons.Default.Done)
}

@Composable
fun ChefNavigation(
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val bottomNavItems = listOf(ChefBottomNavItem.Incoming, ChefBottomNavItem.Accepted)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
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
        }
    }
}
