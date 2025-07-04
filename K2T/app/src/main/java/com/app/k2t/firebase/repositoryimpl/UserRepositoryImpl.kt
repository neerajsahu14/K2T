package com.app.k2t.firebase.repositoryimpl

import android.util.Log.e
import com.app.k2t.firebase.model.User
import com.app.k2t.firebase.repository.UserRepository
import com.app.k2t.firebase.utils.DocumentName.USER
import com.google.firebase.firestore.FirebaseFirestore
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
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun checkUser(userId: String): Boolean {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }

   override suspend fun getUserByEmail(emailId: String): User? {
       return try {
           val document = usersCollection.document(emailId).get().await()
           if (document.exists()) {
               document.toObject(User::class.java)
           } else {
               null
           }
       } catch (e: Exception) {
           null
       }
   }
}