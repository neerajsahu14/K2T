package com.app.k2t.firebase.repository

import com.app.k2t.firebase.model.Food
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for interacting with Food data.
 * This repository provides methods for adding, updating, deleting, and retrieving Food items.
 */
interface FoodRepository {
    suspend fun addFood(food: Food): Boolean
    suspend fun updateFood(foodId: String, food: Food): Boolean
    suspend fun deleteFood(foodId: String): Boolean
    fun getFood(foodId: String): Flow<Food?>
    suspend fun getAllFoods(): Flow<List<Food>>
    suspend fun upsertFood(food: Food): Boolean

}
