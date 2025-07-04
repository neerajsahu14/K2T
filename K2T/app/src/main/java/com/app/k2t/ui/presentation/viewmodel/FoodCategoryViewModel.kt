package com.app.k2t.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.k2t.firebase.model.FoodCategory
import com.app.k2t.firebase.repositoryimpl.FoodCategoryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FoodCategoryViewModel : ViewModel() , KoinComponent{

    private val repository : FoodCategoryRepositoryImpl by inject()

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
            try {
                repository.getAllFoodCategories().collect { categoryList ->
                    _categories.value = categoryList
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch categories: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createCategory(category: FoodCategory) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.createFoodCategory(category)
                fetchCategories() // Refresh categories after creating a new one
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
            try {
                repository.updateFoodCategory(category)
                fetchCategories() // Refresh categories after updating
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
            try {
                repository.deleteFoodCategory(categoryId)
                fetchCategories() // Refresh categories after deletion
            } catch (e: Exception) {
                _error.value = "Failed to delete category: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}