package com.app.k2t.ui.presentation.screen.admin.foodcategory

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.k2t.R
import com.app.k2t.firebase.model.Food
import com.app.k2t.ui.presentation.screen.admin.food.LoadingScreen
import com.app.k2t.ui.presentation.viewmodel.FoodCategoryViewModel
import com.app.k2t.ui.presentation.viewmodel.FoodViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    categoryId: String,
    onManageFoodsClick: (String) -> Unit,
    onEditCategoryClick: (String) -> Unit,
    onEditFoodClick: (String) -> Unit,
    onBackClick: () -> Unit,
    foodViewModel: FoodViewModel = koinViewModel(),
    categoryViewModel: FoodCategoryViewModel = koinViewModel()
) {
    val allFoods by foodViewModel.foods.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()
    val categoryLoading by categoryViewModel.loading.collectAsState()

    val category by remember(categoryId, categoryViewModel.categories.value) {
        derivedStateOf { categoryViewModel.categories.value.find { it.id == categoryId } }
    }

    val foodsInCategory = remember(allFoods, category) {
        allFoods.filter { it.foodId in (category?.foodsIds ?: emptyList()) }
            .sortedByDescending { it.createdAt }
    }

    val error by foodViewModel.error.collectAsState()
    var showRemoveConfirmation by remember { mutableStateOf(false) }
    var foodToRemove by remember { mutableStateOf<Food?>(null) }

    // Add dialog for visibility confirmation
    var showVisibilityConfirmation by remember { mutableStateOf(false) }

    if (showRemoveConfirmation && foodToRemove != null) {
        AlertDialog(
            onDismissRequest = { showRemoveConfirmation = false },
            title = { Text("Confirm Removal") },
            text = { Text("Are you sure you want to remove '${foodToRemove?.name}' from this category?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        categoryViewModel.toggleFoodInCategory(categoryId, foodToRemove?.foodId ?: "")
                        showRemoveConfirmation = false
                        foodToRemove = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Visibility confirmation dialog
    if (showVisibilityConfirmation && category != null) {
        AlertDialog(
            onDismissRequest = { showVisibilityConfirmation = false },
            title = { Text("Change Visibility") },
            text = {
                Text(
                    if (category!!.visible)
                        "Are you sure you want to hide this category? It will not be visible to users."
                    else
                        "Are you sure you want to make this category visible to users?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        categoryViewModel.toggleCategoryVisibility(categoryId)
                        showVisibilityConfirmation = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVisibilityConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = category?.name ?: "Category Details",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Visibility toggle button
            category?.let {
                IconButton(
                    onClick = { showVisibilityConfirmation = true }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (it.visible) R.drawable.visibility else R.drawable.visibility_off
                        ),
                        contentDescription = if (it.visible) "Hide Category" else "Show Category",
                        tint = if (it.visible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(onClick = { onEditCategoryClick(categoryId) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Category")
            }
            IconButton(onClick = { onManageFoodsClick(categoryId) }) {
                Icon(painterResource(R.drawable.baseline_featured_play_list_24), contentDescription = "Manage Foods")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                isLoading || categoryLoading -> {
                    LoadingScreen()
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error ?: "An error occurred",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            foodViewModel.clearError()
                            foodViewModel.fetchAllFoods()
                        }) {
                            Text("Try Again")
                        }
                    }
                }
                category == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Category not found", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                foodsInCategory.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.restaurant),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No foods in this category yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap 'Manage Foods' to add items to this category",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onManageFoodsClick(categoryId) }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_featured_play_list_24),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Manage Foods")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Category description card
                            category?.description?.let { description ->
                                if (description.isNotBlank()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Description",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            Text(
                                text = "Foods (${foodsInCategory.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(foodsInCategory.size, key = { foodsInCategory[it].foodId ?: "" }) { index ->
                            val food = foodsInCategory[index]
                            FoodCardForCategory(
                                food = food,
                                onEditClick = { onEditFoodClick(food.foodId ?: "") },
                                onRemoveClick = {
                                    foodToRemove = food
                                    showRemoveConfirmation = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
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
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food image with placeholder handling
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (food.imageUrl != null) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.name,
                        placeholder = painterResource(id = R.drawable.baseline_fastfood_24),
                        error = painterResource(id = R.drawable.baseline_fastfood_24),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_fastfood_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Food details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name ?: "Unnamed Food",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${food.price ?: "N/A"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Show prep time if available
                    food.details?.prepTime?.let { prepTime ->
                        Text(
                            text = " • ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            painter = painterResource(R.drawable.schedule),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = prepTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Availability indicator
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = if (food.availability == true) "Available" else "Not Available",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = if (food.availability == true)
                                painterResource(R.drawable.check_circle)
                            else
                                painterResource(R.drawable.cancel),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (food.availability == true)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        labelColor = if (food.availability == true)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer,
                        leadingIconContentColor = if (food.availability == true)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }

            // Actions menu
            Box {
                FilledTonalIconButton(
                    onClick = { showMenu = true },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Food") },
                        onClick = {
                            onEditClick()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit Food") }
                    )
                    DropdownMenuItem(
                        text = { Text("Remove from Category") },
                        onClick = {
                            onRemoveClick()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Remove from Category") }
                    )
                }
            }
        }
    }
}
