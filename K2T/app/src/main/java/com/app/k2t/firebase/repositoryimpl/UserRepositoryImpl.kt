package com.app.k2t.firebase.repositoryimpl

import android.util.Log.e
import com.app.k2t.firebase.model.User
import com.app.k2t.firebase.repository.UserRepository
import com.app.k2t.firebase.utils.DocumentName.USER
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of [UserRepository] that interacts with Firebase Firestore.
 *
 * @property db An instance of [FirebaseFirestore] used to access the database.
 */
class UserRepositoryImpl(val db: FirebaseFirestore) : UserRepository {
    private val usersCollection = db.collection(USER)

    override suspend fun createUser(user: User): Boolean {
        return try {
            usersCollection.document(user.id.toString()).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateUser(
        userId: String,
        updatedUser: User
    ): Boolean {
        return try {
            usersCollection.document(userId).set(updatedUser).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUser(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                // Only return user if it's valid
                if (user?.isValid == true) user else null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteUser(userId: String): Boolean {
        return try {
            // Implement soft delete by setting isValid to false
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    val updatedUser = user.copy(isValid = false)
                    usersCollection.document(userId).set(updatedUser).await()
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun checkUser(userId: String): Boolean {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.exists() && document.toObject(User::class.java)?.isValid == true
        } catch (e: Exception) {
            false
        }
    }

   override suspend fun getUserByEmail(emailId: String): User? {
       return try {
           val document = usersCollection.document(emailId).get().await()
           if (document.exists()) {
               val user = document.toObject(User::class.java)
               // Only return user if it's valid
               if (user?.isValid == true) user else null
           } else {
               null
           }
       } catch (e: Exception) {
           null
       }
   }

   override suspend fun getAllUsers(): Flow<List<User>> = callbackFlow {
       val listener = usersCollection
           .whereEqualTo("isValid", true) // Only return valid users
           .addSnapshotListener { snapshot, exception ->
               if (exception != null) {
                   close(exception)
                   return@addSnapshotListener
               }
               trySend(snapshot?.toObjects(User::class.java) ?: emptyList())
           }
       awaitClose { listener.remove() }
   }
}