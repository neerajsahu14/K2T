package com.app.k2t.firebase.repository

import com.app.k2t.firebase.model.FoodCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing FoodCategory data.
 *
 * This interface defines the contract for interacting with the data source
 * responsible for storing and retrieving food category information.
 * Implementations of this interface will handle the specific details of
 * data access, such as interacting with a Firebase Firestore database.
 */
interface FoodCategoryRepository {
    suspend fun createFoodCategory(foodCategory: FoodCategory)
    suspend fun getFoodCategoryById(id: String): FoodCategory?
    fun getAllFoodCategories(): Flow<List<FoodCategory>>
    suspend fun updateFoodCategory(foodCategory: FoodCategory)
    suspend fun deleteFoodCategory(id: String)
}
