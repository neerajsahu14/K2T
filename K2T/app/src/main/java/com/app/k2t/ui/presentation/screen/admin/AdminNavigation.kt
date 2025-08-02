package com.app.k2t.ui.presentation.screen.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.k2t.R
import com.app.k2t.ui.presentation.screen.admin.analytics.AnalyticsScreen
import com.app.k2t.ui.presentation.screen.admin.food.AddEditFoodScreen
import com.app.k2t.ui.presentation.screen.admin.food.FoodScreen
import com.app.k2t.ui.presentation.screen.admin.foodcategory.AllCatagoryScreen
import com.app.k2t.ui.presentation.screen.admin.foodcategory.CategoryDetailsScreen
import com.app.k2t.ui.presentation.screen.admin.foodcategory.CreateAndUpdateCategory
import com.app.k2t.ui.presentation.screen.admin.foodcategory.ManageCategoryFoodsScreen

sealed class BottomNavItem(val route: String, val label: String, val icon: Int) {
    object Food : BottomNavItem("FoodScreen", "Foods", R.drawable.food_bank)
    object Categories : BottomNavItem("AllCategoryScreen", "Categories", R.drawable.category)
    object Analytics : BottomNavItem("AnalyticsScreen", "Analytics", R.drawable.analytics)
}

@Composable
fun AdminNavigation(modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()
    val bottomNavItems = listOf(BottomNavItem.Food, BottomNavItem.Categories, BottomNavItem.Analytics)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val contentPadding = PaddingValues()
    // Show bottom navigation only for main screens (not detail screens)
    val showBottomNav = when (currentRoute) {
        BottomNavItem.Food.route, BottomNavItem.Categories.route, BottomNavItem.Analytics.route -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { it }
                ),
                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { it }
                )
            ) {
                NavigationBar(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentDestination by navController.currentBackStackEntryAsState()
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.destination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.15f else 1.0f,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            ),
                            label = "scale"
                        )

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.label,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            scaleX = if (selected) scale else 1f
                                            scaleY = if (selected) scale else 1f
                                        }
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.graphicsLayer {
                                        alpha = if (selected) 1f else 0.7f
                                    }
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Food.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(BottomNavItem.Food.route) {
                FoodScreen(
                    onNavigateToAddEditFood = { foodId ->
                        navController.navigate("AddEditFoodScreen?foodId=$foodId")
                    }
                )
            }
            composable(BottomNavItem.Categories.route) {
                AllCatagoryScreen(
                    onCategoryClick = { category ->
                        val categoryId = category.id
                        navController.navigate("CategoryDetailsScreen/$categoryId")
                    },
                    onAddCategoryClick = {
                        navController.navigate("CreateAndUpdateCategory")
                    }
                )
            }
            composable(
                route = "CategoryDetailsScreen/{categoryId}",
                arguments = listOf(
                    navArgument("categoryId") { nullable = false }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                CategoryDetailsScreen(
                    categoryId = categoryId,
                    onEditFoodClick = { foodId ->
                        navController.navigate("AddEditFoodScreen?foodId=$foodId")
                    },
                    onManageFoodsClick = { catId ->
                        navController.navigate("ManageCategoryFoodsScreen/$catId")
                    },
                    onBackClick = { navController.popBackStack() },
                    onEditCategoryClick = { catId ->
                        navController.navigate("CreateAndUpdateCategory?categoryId=$catId")
                    }
                )
            }
            composable(
                route = "AddEditFoodScreen?foodId={foodId}",
                arguments = listOf(
                    navArgument("foodId") { nullable = true }
                )
            ) { backStackEntry ->
                val foodId = backStackEntry.arguments?.getString("foodId")
                AddEditFoodScreen(
                    foodId = foodId,
                    onDismiss = { navController.popBackStack() },
                )
            }
            composable(
                route = "CreateAndUpdateCategory?categoryId={categoryId}",
                arguments = listOf(
                    navArgument("categoryId") { nullable = true }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")
                CreateAndUpdateCategory(
                    categoryId = categoryId,
                    onDismiss = { navController.popBackStack() }
                )
            }
            composable(BottomNavItem.Analytics.route) {
                AnalyticsScreen()
            }
            composable(
                route = "ManageCategoryFoodsScreen/{categoryId}",
                arguments = listOf(
                    navArgument("categoryId") { nullable = false }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                ManageCategoryFoodsScreen(
                    categoryId = categoryId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
