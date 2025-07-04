package com.app.k2t.firebase.repositoryimpl

import com.app.k2t.firebase.model.Food
import com.app.k2t.firebase.repository.FoodRepository
import com.app.k2t.firebase.utils.DocumentName
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of [FoodRepository] that interacts with Firebase Firestore.
 *
 * @param db The [FirebaseFirestore] instance to use for database operations.
 */
class FoodRepositoryImpl(private val db: FirebaseFirestore) : FoodRepository {

    private val foodCollection = db.collection(DocumentName.FOOD)

    override suspend fun addFood(food: Food): Boolean {
        return try {
            foodCollection.add(food).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateFood(foodId: String, food: Food): Boolean {
        return try {
            foodCollection.document(foodId).set(food).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteFood(foodId: String): Boolean {
        return try {
            foodCollection.document(foodId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getFood(foodId: String): Flow<Food?> = callbackFlow {
        if (foodId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = foodCollection.document(foodId).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(Food::class.java))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getAllFoods(): Flow<List<Food>> = callbackFlow {
        val listener = foodCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObjects(Food::class.java) ?: emptyList())
        }
        awaitClose { listener.remove() }
    }
    override suspend fun upsertFood(food: Food): Boolean {
        return try {
            val docRef = if (food.foodId.isNullOrBlank()) {
                // For a new food, create a document reference with a new ID
                foodCollection.document()
            } else {
                // For an existing food, use its ID
                foodCollection.document(food.foodId!!)
            }
            // Set the ID on the food object itself, so it's stored in the document
            food.foodId = docRef.id
            docRef.set(food).await()
            true
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            false
        }
    }
}
