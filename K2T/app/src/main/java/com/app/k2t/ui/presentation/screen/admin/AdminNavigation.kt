package com.app.k2t.ui.presentation.screen.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.k2t.R
import com.app.k2t.ui.presentation.screen.admin.foodcategory.AllCatagoryScreen
import com.app.k2t.ui.presentation.screen.admin.foodcategory.CategoryDetailsScreen
import com.app.k2t.ui.presentation.screen.admin.foodcategory.CreateAndUpdateCategory
import com.app.k2t.ui.presentation.screen.admin.food.AddEditFoodScreen
import com.app.k2t.ui.presentation.screen.admin.food.FoodScreen

sealed class BottomNavItem(val route: String, val label: String, val icon: Int) {
    object Food : BottomNavItem("FoodScreen", "Foods", R.drawable.food_bank)
    object Categories : BottomNavItem("AllCategoryScreen", "Categories", R.drawable.category)
}

@Composable
fun AdminNavigation(modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()
    val bottomNavItems = listOf(BottomNavItem.Food, BottomNavItem.Categories)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = bottomNavItems)
        }
    ) { it
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Food.route,
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
                        val foodsId = category.foodsIds.joinToString(",")
                        navController.navigate("CategoryDetailsScreen?categoryId=$categoryId&foodsId=$foodsId")
                    },
                    onAddCategoryClick = {
                        navController.navigate("CreateAndUpdateCategory")
                    }
                )
            }
            composable(
                route = "CategoryDetailsScreen?categoryId={categoryId}&foodsId={foodsId}",
                arguments = listOf(
                    navArgument("categoryId") { nullable = false },
                    navArgument("foodsId") { nullable = false }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                val foodsId = backStackEntry.arguments?.getString("foodsId")?.split(",") ?: emptyList()
                CategoryDetailsScreen(
                    categoryId = categoryId,
                    foodsId = foodsId,
                    onAddFoodClick = { foodId ->
                        navController.navigate("AddEditFoodScreen?foodId=$foodId")
                    },
                    onEditFoodClick = { foodId ->
                        navController.navigate("AddEditFoodScreen?foodId=$foodId")
                    },
                    onBackClick = { navController.popBackStack() }
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
                    onSave = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<BottomNavItem>) {
    NavigationBar {
        val currentDestination by navController.currentBackStackEntryAsState()
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.destination?.hierarchy?.any { it.route == item.route } == true,
                onClick = { navController.navigate(item.route) },
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
