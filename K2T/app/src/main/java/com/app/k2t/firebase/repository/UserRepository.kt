package com.app.k2t.firebase.repository

import com.app.k2t.firebase.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining operations for managing user data in a repository.
 *
 * This interface provides methods for creating, updating, retrieving, and checking the existence of users.
 * It also includes a method to fetch a user based on their email address.
 * All operations are designed to be performed asynchronously using coroutines.
 */
interface UserRepository {
    suspend fun createUser(user: User): Boolean
    suspend fun updateUser(userId: String, updatedUser: User): Boolean
    suspend fun getUser(userId: String): User?
    suspend fun checkUser(userId: String): Boolean
    suspend fun getUserByEmail(emailId : String) : User?
    suspend fun deleteUser(userId: String): Boolean
    suspend fun getAllUsers(): Flow<List<User>>
}
