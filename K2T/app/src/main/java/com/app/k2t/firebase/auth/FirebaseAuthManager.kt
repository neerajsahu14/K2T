package com.app.k2t.firebase.auth

import com.app.k2t.firebase.model.User
import com.app.k2t.firebase.repositoryimpl.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepositoryImpl
) {
    suspend fun loginWithEmailPassword(
        email: String,
        password: String,
        onSuccess: (firebaseUser: FirebaseUser?, userDetails: User?) -> Unit, // Return both FirebaseUser and User details
        onFailure: (String) -> Unit
    ) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                // After successful authentication, fetch user details from Firestore
                val userDetails = userRepository.getUser(firebaseUser.uid)
                onSuccess(firebaseUser, userDetails)
            } else {
                // Should not happen if signInWithEmailAndPassword succeeds
                onFailure("Authentication successful but user ID not found.")
            }
        } catch (e: Exception) {
            onFailure(e.message ?: "Login failed.")
        }
    }

      suspend fun registerUser(
        email: String,
        password: String,
        userDetails: User, // Receive full user details
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUserId = result.user?.uid
            if (firebaseUserId != null) {
                // Use the UID from Firebase Auth as the ID for the User object
                val newUserWithId = userDetails.copy(id = firebaseUserId) // Ensure email is also set from auth
                val success = userRepository.createUser(newUserWithId)
                if (success) {
                    onSuccess()
                } else {
                    // Optionally, delete the Firebase Auth user if Firestore save fails
                     result.user?.delete()?.await() // Requires careful error handling
                    onFailure("Failed to save user data to Firestore.")
                }
            } else {
                onFailure("Firebase user creation succeeded but UID not found.")
            }
        } catch (e: Exception) {
            onFailure(e.message ?: "Registration failed.")
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }

}
