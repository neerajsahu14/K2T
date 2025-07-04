package com.app.k2t.ui.presentation.screen.admin.foodcategory

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.k2t.firebase.model.FoodCategory
import com.app.k2t.ui.presentation.viewmodel.FoodCategoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateAndUpdateCategory(
    modifier: Modifier = Modifier,
    categoryId: String? = null,
    viewModel: FoodCategoryViewModel = koinViewModel(),
    onSave: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            val category = viewModel.categories.value.find { it.id == categoryId }
            if (category != null) {
                name = category.name
                description = category.description
            }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Category Name")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Description")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val category = FoodCategory(
                    id = categoryId ?: System.currentTimeMillis().toString(),
                    name = name,
                    description = description,
                    imageUrl = "",
                    priority = 0,
                    foodsIds = emptyList(),
                    createdAt = null
                )
                if (categoryId == null) {
                    viewModel.createCategory(category)
                } else {
                    viewModel.updateCategory(category)
                }
                onSave()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (categoryId == null) "Create" else "Update")
        }
    }
}
