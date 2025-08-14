package com.app.k2t.firebase.model

import com.google.firebase.Timestamp

/**
 * Represents a category of food items.
 *
 * This data class holds information about a specific food category, including its
 * identifier, display name, visual representation, and associated food items.
 *
 * @property id The unique identifier for this food category.
 * @property name The display name of the food category (e.g., "Appetizers", "Desserts").
 * @property imageUrl The URL of an image representing this food category.
 * @property description A brief description of the food category.
 * @property priority An integer value indicating the display order or importance of this category.
 *                   Lower numbers might indicate higher priority.
 * @property foodsIds A list of strings, where each string is the unique identifier (ID) of a
 *                    food item belonging to this category.
 * @property visible A boolean flag indicating whether this category should be visible to users.
 *                   Defaults to `true`.
 * @property createdAt A [Timestamp] indicating when this food category was created or last updated.
 *                     This can be null if the creation timestamp is not available or applicable.
 * @property valid A [Boolean] indicating the [category] is deleted  or not.
 */
data class FoodCategory(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val priority: Int = 0,
    val foodsIds: List<String> = emptyList(),
    val visible: Boolean = true,
    val createdAt: Timestamp? = null,
    val valid: Boolean = true // Indicates if the category is valid or softly deleted
)
