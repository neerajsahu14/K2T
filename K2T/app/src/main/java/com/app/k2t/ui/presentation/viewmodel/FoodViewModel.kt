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
import com.app.k2t.ui.presentation.screen.admin.food.AddEditFoodUiState
import com.app.k2t.firebase.model.Details
import com.app.k2t.firebase.model.Nutrition
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update

class FoodViewModel : ViewModel(), KoinComponent {
    private val foodRepository: FoodRepositoryImpl by inject()
    private val cloudinaryManager: CloudinaryManager by inject()

    // State for AddEditFoodScreen
    private val _uiState = MutableStateFlow(AddEditFoodUiState())
    val uiState: StateFlow<AddEditFoodUiState> = _uiState.asStateFlow()

    private var _loadedFoodId: String? = null // To store the ID of the food being edited

    // Existing states
    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    val foods: StateFlow<List<Food>> = _foods.asStateFlow()

    private val _isLoading = MutableStateFlow(false) // General loading for list, etc.
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false) // Specific for save operations
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        fetchAllFoods() // Keep this if used elsewhere, or for a list view
    }

    fun loadInitialFood(foodId: String?) {
        _loadedFoodId = foodId
        if (foodId == null) {
            _uiState.value = AddEditFoodUiState() // Reset for new food
            return
        }
        viewModelScope.launch {
            val foodItem = foodRepository.getFood(foodId).firstOrNull() // Collect the first emission
            if (foodItem != null) {
                _uiState.value = AddEditFoodUiState(
                    name = foodItem.name ?: "",
                    price = foodItem.price?.toString() ?: "",
                    ingredientsList = foodItem.details?.ingredients ?: emptyList(),
                    currentIngredient = "",
                    existingImageUrl = foodItem.imageUrl,
                    selectedImageUri = null,
                    videoUrl = foodItem.videoUrl ?: "",
                    availability = foodItem.availability ?: true,
                    isVeg = foodItem.isVeg ?: true,
                    description = foodItem.details?.description ?: "",
                    allergensList = foodItem.details?.allergens ?: emptyList(),
                    currentAllergen = "",
                    servingSize = foodItem.nutrition?.servingSize ?: "",
                    calories = foodItem.nutrition?.calories?.toString() ?: "",
                    protein = foodItem.nutrition?.protein?.toString() ?: "",
                    carbohydrates = foodItem.nutrition?.carbohydrates?.toString() ?: "",
                    fat = foodItem.nutrition?.fat?.toString() ?: "",
                    showValidationErrors = false
                )
            } else {
                _uiState.value = AddEditFoodUiState() // Reset if food not found
                _error.value = "Food item not found."
            }
        }
    }

    // --- UI Event Handlers ---
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onPriceChange(newPrice: String) {
        _uiState.update { it.copy(price = newPrice) }
    }


    fun onCurrentIngredientChange(newCurrentIngredient: String) {
        _uiState.update { it.copy(currentIngredient = newCurrentIngredient) }
    }

    fun addIngredient() {
        val currentIngredient = _uiState.value.currentIngredient
        if (currentIngredient.isNotBlank()) {
            _uiState.update {
                it.copy(
                    ingredientsList = it.ingredientsList + currentIngredient.trim(),
                    currentIngredient = ""
                )
            }
        }
    }

    fun removeIngredient(ingredient: String) {
        _uiState.update {
            it.copy(ingredientsList = it.ingredientsList - ingredient)
        }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun onVideoUrlChange(newVideoUrl: String) {
        _uiState.update { it.copy(videoUrl = newVideoUrl) }
    }

    fun onAvailabilityChange(newAvailability: Boolean) {
        _uiState.update { it.copy(availability = newAvailability) }
    }

    fun onIsVegChange(newIsVeg: Boolean) {
        _uiState.update { it.copy(isVeg = newIsVeg) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onCurrentAllergenChange(newCurrentAllergen: String) {
        _uiState.update { it.copy(currentAllergen = newCurrentAllergen) }
    }

    fun addAllergen() {
        val currentAllergen = _uiState.value.currentAllergen
        if (currentAllergen.isNotBlank()) {
            _uiState.update {
                it.copy(
                    allergensList = it.allergensList + currentAllergen.trim(),
                    currentAllergen = ""
                )
            }
        }
    }

    fun removeAllergen(allergen: String) {
        _uiState.update {
            it.copy(allergensList = it.allergensList - allergen)
        }
    }

    fun onServingSizeChange(newServingSize: String) {
        _uiState.update { it.copy(servingSize = newServingSize) }
    }

    fun onCaloriesChange(newCalories: String) {
        _uiState.update { it.copy(calories = newCalories) }
    }

    fun onProteinChange(newProtein: String) {
        _uiState.update { it.copy(protein = newProtein) }
    }

    fun onCarbohydratesChange(newCarbohydrates: String) {
        _uiState.update { it.copy(carbohydrates = newCarbohydrates) }
    }

    fun onFatChange(newFat: String) {
        _uiState.update { it.copy(fat = newFat) }
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

    fun updateFood(foodId: String, food: Food) {
        viewModelScope.launch {
            try {
                _isSaving.value = true
                _error.value = null
                foodRepository.updateFood(foodId, food)
                _isSaving.value = false
            } catch (e: Exception) {
                _error.value = "Failed to update food: ${e.message}"
                _isSaving.value = false
            }
        }
    }

    fun deleteFood(foodId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val result = foodRepository.deleteFood(foodId)
                if (!result) {
                    _error.value = "Failed to delete food"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to delete food: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun saveFoodDetails() {
        _uiState.update { it.copy(showValidationErrors = true) }
        if (!_uiState.value.isFormValid) {
            _error.value = "Please fill all required fields correctly."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                val currentState = _uiState.value
                var foodToSave = Food(
                    foodId = _loadedFoodId,
                    name = currentState.name.takeIf { it.isNotBlank() },
                    price = currentState.price.toDoubleOrNull(),
                    details = Details(
                        description = currentState.description.takeIf { it.isNotBlank() },
                        ingredients = currentState.ingredientsList.ifEmpty { null },
                        allergens = currentState.allergensList.ifEmpty { null }
                    ),
                    nutrition = Nutrition(
                        servingSize = currentState.servingSize.takeIf { it.isNotBlank() },
                        calories = currentState.calories.toDoubleOrNull(),
                        protein = currentState.protein.toDoubleOrNull(),
                        carbohydrates = currentState.carbohydrates.toDoubleOrNull(),
                        fat = currentState.fat.toDoubleOrNull()
                    ),
                    isVeg = currentState.isVeg,
                    imageUrl = currentState.existingImageUrl,
                    videoUrl = currentState.videoUrl.takeIf { it.isNotBlank() },
                    availability = currentState.availability,
                    valid = true
                )

                val finalImageUrl = if (currentState.selectedImageUri != null) {
                    cloudinaryManager.uploadImage(currentState.selectedImageUri)
                } else {
                    currentState.existingImageUrl
                }
                foodToSave = foodToSave.copy(imageUrl = finalImageUrl)

                if (_loadedFoodId != null) {
                    val existingFood = foodRepository.getFood(_loadedFoodId!!).firstOrNull()
                    if (existingFood != null) {
                        foodToSave = foodToSave.copy(createdAt = existingFood.createdAt)
                    }
                }
                // upsertFood in repository should handle if foodId is null (new) or existing (update)
                foodRepository.upsertFood(foodToSave)
                // If successful, onDismiss will be called from the screen, which should handle navigation.
                // We can also introduce a success StateFlow/SharedFlow if more complex post-save UI logic is needed.
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to save food."
            } finally {
                _isSaving.value = false
            }
        }
    }


    fun getFood(foodId: String): Flow<Food?> = foodRepository.getFood(foodId)

    fun clearError() {
        _error.value = null
    }
}