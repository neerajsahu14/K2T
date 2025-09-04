package com.app.k2t.ui.presentation.screen.chef.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodStatusScreen(
    modifier: Modifier = Modifier,
    foodViewModel: FoodViewModel = koinViewModel()
) {
    val foods by foodViewModel.foods.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()
    val paddingValues = PaddingValues()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (isLoading && foods.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surfaceContainerLow),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(foods, key = { it.foodId ?: "" }) { food ->
                    food.foodId?.let {
                        FoodStatusCard(
                            food = food,
                            onAvailabilityChange = { newAvailability ->
                                foodViewModel.updateFood(
                                    it,
                                    food.copy(availability = newAvailability)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

}