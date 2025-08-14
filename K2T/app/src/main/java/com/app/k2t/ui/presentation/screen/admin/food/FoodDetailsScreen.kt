package com.app.k2t.ui.presentation.screen.admin.food

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.k2t.firebase.model.Food
import com.app.k2t.ui.presentation.screen.shared.FoodDetailsContent
import com.app.k2t.ui.theme.K2TTheme
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreen(
    foodId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    foodViewModel: FoodViewModel = koinViewModel()
) {
    var food by remember { mutableStateOf<Food?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) } // This can be triggered by parent
    val scrollState = rememberScrollState()

    // Collect food data
    LaunchedEffect(foodId) {
        foodViewModel.getFood(foodId).collect { foodData ->
            food = foodData
            isLoading = false
        }
    }

    // Collect error state
    val viewModelError by foodViewModel.error.collectAsState()
    val isDeletingOrSaving by foodViewModel.isLoading.collectAsState() // Tracks VM's general loading/saving

    LaunchedEffect(viewModelError) {
        error = viewModelError
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${food?.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        foodViewModel.deleteFood(foodId) // Assumes deleteFood handles isDeletingOrSaving state
                        showDeleteConfirmation = false
                        onDeleteSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Error dialog
    if (error != null) {
        AlertDialog(
            onDismissRequest = { foodViewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error ?: "An unknown error occurred") },
            confirmButton = {
                Button(onClick = { foodViewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    // The main content area.
    // The outer Scaffold (from navigation) should provide appropriate background color and padding.
    Column(
        modifier = modifier
            .fillMaxSize() // Default padding, adjust as needed or rely on outer Scaffold
    ) {
        when {
            isLoading -> {
                LoadingIndicator()
            }
            food == null -> {
                EmptyState(message = "Food not found or has been deleted.")
            }
            else -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        IconButton(onClick = { onEditClick(foodId) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }
                }
                FoodDetailsContent(
                    food = food!!, // food is checked for nullity above
                    scrollState = scrollState, // This scrollState is for FoodDetailsContent internal scroll
                    isAdminView = true,
                    showNutritionDetails = true
                    // contentPadding can be removed or adjusted, as overall padding is on the Column
                    // contentPadding = PaddingValues(bottom = 16.dp)
                )

                // Loading overlay for operations like delete/update initiated from this screen
                // or observed via isDeletingOrSaving from ViewModel
                if (isDeletingOrSaving) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(), // This will overlay on top of the content within the Column
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp), // Increased size slightly
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            shadowElevation = 8.dp // Increased elevation
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Processing...",
                                    style = MaterialTheme.typography.bodyMedium // Slightly larger text
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
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp)) // Slightly larger
        Spacer(modifier = Modifier.height(8.dp))
        Text("Loading details...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Info, // Consider a more specific icon like ReportProblem or CloudOff
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall, // More prominent
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please check your connection or try again later.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFoodDetailsScreenLoading() {
    K2TTheme {
        // Simulate loading state for preview
        val mockViewModel = koinViewModel<FoodViewModel>() // Basic Koin VM for preview
        FoodDetailsScreen(foodId = "preview", foodViewModel = mockViewModel)
        // To truly preview loading, you'd mock the ViewModel's getFood to delay or set isLoading
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFoodDetailsScreenEmpty() {
     K2TTheme {
        // Simulate empty state for preview
        val mockViewModel = koinViewModel<FoodViewModel>()
        // How to make food null and isLoading false for preview?
        // One way: provide a mock VM that returns null food and isLoading false.
        // For simplicity, directly showing EmptyState might be easier for isolated preview.
        EmptyState(message = "Food not found or has been deleted.")
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFoodDetailsScreenWithData() {
    K2TTheme {
         val food = Food(foodId = "1", name = "Preview Pasta", price = 12.99, isVeg = true, availability = true, details = com.app.k2t.firebase.model.Details(description = "Delicious preview pasta with rich tomato sauce.", ingredients = listOf("Pasta", "Tomato Sauce", "Cheese")), nutrition = com.app.k2t.firebase.model.Nutrition(servingSize = "250g", calories = 450.0))
        // This preview will be limited as it doesn't run LaunchedEffect with a real VM
        // For a full data preview, you'd need a more elaborate mock VM setup.
        // Or, more simply, preview FoodDetailsContent directly.
        FoodDetailsContent(food = food, scrollState = rememberScrollState(), isAdminView = true, showNutritionDetails = true)
    }
}
