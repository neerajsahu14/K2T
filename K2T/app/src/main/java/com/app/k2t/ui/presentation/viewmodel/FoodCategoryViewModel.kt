package com.app.k2t.ui.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.k2t.cloudinary.CloudinaryManager
import com.app.k2t.firebase.model.FoodCategory
import com.app.k2t.firebase.repositoryimpl.FoodCategoryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FoodCategoryViewModel : ViewModel() , KoinComponent {

    private val repository: FoodCategoryRepositoryImpl by inject()

    private val _categories = MutableStateFlow<List<FoodCategory>>(emptyList())
    val categories: StateFlow<List<FoodCategory>> = _categories

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchCategories() // Ensure categories are fetched when the ViewModel is initialized
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            repository.getAllFoodCategories().collect { categoryList ->
                _categories.value = categoryList
                if (_loading.value) {
                    _loading.value = false
                }
            }
        }
    }

    fun createCategory(category: FoodCategory) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.createFoodCategory(category)
            } catch (e: Exception) {
                _error.value = "Failed to create category: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateCategory(category: FoodCategory) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.updateFoodCategory(category)
            } catch (e: Exception) {
                _error.value = "Failed to update category: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.deleteFoodCategory(categoryId)
            } catch (e: Exception) {
                _error.value = "Failed to delete category: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleFoodInCategory(categoryId: String, foodId: String) {
        viewModelScope.launch {
            val category = _categories.value.find { it.id == categoryId }
            if (category != null) {
                val updatedFoodIds = category.foodsIds.toMutableList()
                if (updatedFoodIds.contains(foodId)) {
                    updatedFoodIds.remove(foodId)
                } else {
                    updatedFoodIds.add(foodId)
                }
                val updatedCategory = category.copy(foodsIds = updatedFoodIds)
                updateCategory(updatedCategory)
            } else {
                _error.value = "Category not found to toggle food."
            }
        }
    }

    fun toggleCategoryVisibility(categoryId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val category = _categories.value.find { it.id == categoryId }
            if (category != null) {
                // Toggle the visibility status
                val updatedCategory = category.copy(visible = !category.visible)
                updateCategory(updatedCategory)
            } else {
                _error.value = "Category not found."
            }
            _loading.value = false
        }
    }

    suspend fun uploadCategoryImage(context: Context, imageUri: Uri): String? {
        return try {
            val url = CloudinaryManager(context).uploadImage(imageUri)
            url
        } catch (e: Exception) {
            _error.value = "Image upload failed: ${e.message}"
            null
        }
    }

}