package com.app.k2t.ui.presentation.screen.table.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.k2t.firebase.model.Food
import com.app.k2t.local.model.FoodInCart
import com.app.k2t.ui.presentation.viewmodel.CartViewModel
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableHomeScreen(
    modifier: Modifier = Modifier,
    foodViewModel: FoodViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    onFoodClick: (Food) -> Unit = {},
    onCartClick: () -> Unit = {},
    onAddToCart: (Food) -> Unit = {}
) {
    val foods by foodViewModel.foods.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()
    val error by foodViewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }


    fun insertInCart(food: Food) {
        var newFoodToCart = FoodInCart(
            foodId = food.foodId.toString(),
            quantity = 1,
            foodName = food.name.toString(),
            unitPrice = food.price,
            totalPrice = food.price,
            imageUrl = food.imageUrl
        )
       cartViewModel.insertFood(newFoodToCart)
    }
    // Filter foods based on search and category
    val filteredFoods = remember(foods, searchQuery, selectedCategory) {
        foods.filter { food ->
            val matchesSearch = food.name?.contains(searchQuery, ignoreCase = true) == true ||
                    food.details?.ingredients?.any { it.contains(searchQuery, ignoreCase = true) } == true
            val matchesCategory = selectedCategory == "All"
            val isAvailable = food.availability == true

            matchesSearch && matchesCategory && isAvailable
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search food items...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )



        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading delicious food...")
                    }
                }
            }

            error != null -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = "Unable to load menu: $error",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            filteredFoods.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedCategory != "All") {
                                "No food items match your search"
                            } else {
                                "No food items available"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try adjusting your search or category filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredFoods){ food ->
                        TableFoodCard(
                            food = food,
                            onClick = { onFoodClick(food) },
                            onAddToCart = { insertInCart(it) }
                        )

                    }
                }
            }
        }
    }
}

@Preview(name = "TableHomeScreen")
@Composable
private fun PreviewCustomerHomeScreen() {
    TableHomeScreen()
}

