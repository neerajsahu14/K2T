package com.app.k2t.ui.presentation.screen.admin.foodcategory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.k2t.firebase.model.Food
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    categoryId: String,
    foodsId: List<String>,
    onAddFoodClick: (String) -> Unit,
    onEditFoodClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: FoodViewModel = koinViewModel()
) {
    val foods = foodsId.map { foodId -> viewModel.getFood(foodId).collectAsState(initial = null).value }
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Category Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onAddFoodClick(categoryId) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Food")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                error != null -> {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                foods.isEmpty() -> {
                    Text(
                        text = "No foods available in this category",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(foods.size) { index ->
                            val food = foods[index]
                            if (food != null) {
                                FoodCardForCategory(
                                    food = food,
                                    onEditClick = { onEditFoodClick(food.foodId ?: "") },
                                    onDeleteClick = { viewModel.deleteFood(food.foodId ?: "") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodCardForCategory(
    food: Food,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = food.name ?: "Unnamed Food", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Price: $${food.price ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Food")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Food")
                }
            }
        }
    }
}
