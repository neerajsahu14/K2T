package com.app.k2t.ui.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.k2t.cloudinary.CloudinaryManager
import com.app.k2t.firebase.model.Food
import com.app.k2t.firebase.repositoryimpl.FoodRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FoodViewModel : ViewModel(), KoinComponent {
    private val foodRepository: FoodRepositoryImpl by inject()
    private val cloudinaryManager: CloudinaryManager by inject()

    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods: StateFlow<List<Food>> = _foods.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        fetchAllFoods()
    }

    fun fetchAllFoods() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                foodRepository.getAllFoods().collect { foodList ->
                    _foods.value = foodList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _isLoading.value = false
            }
        }
    }

    fun addFood(food: Food) {
        viewModelScope.launch {
            try {
                _error.value = null
                foodRepository.addFood(food)
            } catch (e: Exception) {
                _error.value = "Failed to add food: ${e.message}"
            }
        }
    }

    fun updateFood(foodId: String, food: Food) {
        viewModelScope.launch {
            try {
                _error.value = null
                foodRepository.updateFood(foodId, food)
            } catch (e: Exception) {
                _error.value = "Failed to update food: ${e.message}"
            }
        }
    }

    fun deleteFood(foodId: String) {
        viewModelScope.launch {
            try {
                _error.value = null
                foodRepository.deleteFood(foodId)
            } catch (e: Exception) {
                _error.value = "Failed to delete food: ${e.message}"
            }
        }
    }
    fun upsertFood(food: Food) {
        viewModelScope.launch {
            try {
                _error.value = null
                foodRepository.upsertFood(food)
            } catch (e: Exception) {
                _error.value = "Failed to upsert food: ${e.message}"
            }
        }
    }

    suspend fun upsertFoodWithImage(food: Food, imageUri: Uri?) {
        try {
            _isSaving.value = true
            _error.value = null

            val imageUrl = if (imageUri != null) {
                cloudinaryManager.uploadImage(imageUri)
            } else {
                food.imageUrl
            }

            val foodToSave = food.copy(imageUrl = imageUrl)
            foodRepository.upsertFood(foodToSave)

        } catch (e: Exception) {
            _error.value = "Failed to save food: ${e.message}"
            throw e // Re-throw exception to be caught in UI
        } finally {
            _isSaving.value = false
        }
    }

    fun getFood(foodId: String): Flow<Food?> = foodRepository.getFood(foodId)

    fun clearError() {
        _error.value = null
    }
}