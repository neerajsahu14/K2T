package com.app.k2t.firebase.repositoryimpl

import com.app.k2t.firebase.model.FoodCategory
import com.app.k2t.firebase.repository.FoodCategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of [FoodCategoryRepository] using Firebase Firestore.
 * This class provides methods to interact with the "categories" collection in Firestore
 * for managing food categories.
 */
class FoodCategoryRepositoryImpl : FoodCategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("categories")

    override fun getAllFoodCategories(): Flow<List<FoodCategory>> = callbackFlow {
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
        collection.document(foodCategory.id).set(foodCategory).await()
    }

    override suspend fun getFoodCategoryById(id: String): FoodCategory? {
        val document = collection.document(id).get().await()
        return document.toObject(FoodCategory::class.java)
    }

    override suspend fun updateFoodCategory(foodCategory: FoodCategory) {
        collection.document(foodCategory.id).set(foodCategory).await()
    }

    override suspend fun deleteFoodCategory(id: String) {
        collection.document(id).delete().await()
    }
}
