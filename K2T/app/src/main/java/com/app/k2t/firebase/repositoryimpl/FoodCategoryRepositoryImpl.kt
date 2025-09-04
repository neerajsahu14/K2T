package com.app.k2t.firebase.repositoryimpl

import com.app.k2t.firebase.model.FoodCategory
import com.app.k2t.firebase.repository.FoodCategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Implementation of [FoodCategoryRepository] using Firebase Firestore.
 * This class provides methods to interact with the "categories" collection in Firestore
 * for managing food categories.
 */
class FoodCategoryRepositoryImpl : FoodCategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("categories")

    /**
     * Returns a flow of all valid food categories (not soft deleted)
     */
    override fun getAllFoodCategories(): Flow<List<FoodCategory>> = callbackFlow {
        val listener = collection
            .whereEqualTo("valid", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val categories = snapshot?.toObjects(FoodCategory::class.java) ?: emptyList()
                trySend(categories)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Returns a flow of all food categories including soft deleted ones
     */
    fun getAllFoodCategoriesIncludingDeleted(): Flow<List<FoodCategory>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val categories = snapshot?.toObjects(FoodCategory::class.java) ?: emptyList()
            trySend(categories)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createFoodCategory(foodCategory: FoodCategory) {
        // Ensure new categories are valid by default
        val categoryToSave = foodCategory.copy(valid = true)
        collection.document(categoryToSave.id).set(categoryToSave).await()
    }

    override suspend fun getFoodCategoryById(id: String): FoodCategory? {
        val document = collection.document(id).get().await()
        return document.toObject(FoodCategory::class.java)
    }

    override suspend fun updateFoodCategory(foodCategory: FoodCategory) {
        collection.document(foodCategory.id).set(foodCategory).await()
    }

    /**
     * Soft deletes a food category by setting valid=false
     */
    override suspend fun deleteFoodCategory(id: String) {
        // Soft delete - set valid to false
        collection.document(id).update("valid", false).await()
    }

    /**
     * Permanently removes a food category from Firestore
     */
    override suspend fun permanentlyDeleteFoodCategory(id: String) {
        collection.document(id).delete().await()
    }

    /**
     * Restores a soft-deleted food category
     */
    override suspend fun restoreFoodCategory(id: String) {
        collection.document(id).update("valid", true).await()
    }
}