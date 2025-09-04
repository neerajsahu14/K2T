package com.app.k2t.firebase.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a user in the system.
 *
 * @property id The unique identifier of the user. Applicable to all roles.
 * @property name The name of the user. Applicable to waiter, chef, and admin roles.
 * @property role The role of the user. Possible values are "table", "chef", "waiter", "admin".
 * @property tableNumber The table number assigned to the user, if the role is "table".
 * @property tableId The unique identifier of the table assigned to the user, if the role is "table".
 * @property isActive Indicates whether the user account is active. Applicable to all roles. Defaults to true.
 * @property createdAt The timestamp when the user was created. This is automatically set by the server. Applicable to all roles.
 */
data class User(
    val id: String? = null, // All roles
    val name: String? = null, // waiter, chef, admin
    val role: String? = null, // "table", "chef", "waiter", "admin"
    val tableNumber: String? = null, // Only for table
    val tableId: String? = null, // Only for table
    val isActive: Boolean = true, // All roles
    @ServerTimestamp
    var createdAt: Date? = null, // All roles
    val isValid : Boolean = true
)
