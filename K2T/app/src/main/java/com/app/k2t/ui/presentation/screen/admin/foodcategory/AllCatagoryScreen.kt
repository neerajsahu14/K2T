package com.app.k2t.ui.presentation.screen.admin.foodcategory

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.app.k2t.firebase.model.FoodCategory
import com.app.k2t.ui.presentation.viewmodel.FoodCategoryViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCatagoryScreen(
    modifier: Modifier = Modifier,
    viewModel: FoodCategoryViewModel = koinViewModel(),
    onCategoryClick: (FoodCategory) -> Unit,
    onAddCategoryClick: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCategoryClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier.padding(paddingValues)
        ) {
            items(categories.size) { index ->
                val category = categories[index]
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}
