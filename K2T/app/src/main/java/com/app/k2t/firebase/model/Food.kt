package com.app.k2t.firebase.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents a food item in the system.
 *
 * This data class holds all information about a specific food item, including its name,
 * price, availability, and whether it is vegetarian. It also contains nested objects for
 * detailed descriptions and nutritional facts.
 *
 * @property foodId The unique identifier for this food item from Firestore.
 * @property name The name of the dish.
 * @property price The price of the food item.
 * @property availability Indicates if the food item is currently available.
 * @property isVeg **Indicates if the food is vegetarian (true) or non-vegetarian (false).**
 * @property details A nested object containing descriptive details like ingredients.
 * @property nutrition A nested object containing nutritional information.
 * @property imageUrl The URL of an image representing the food item.
 * @property createdAt A timestamp indicating when the food item was created.
 */
data class Food(
    @DocumentId
    var foodId: String? = null,
    var name: String? = null,
    var price: Double? = null,
    var availability: Boolean? = true,
    var isVeg: Boolean? = true, // Handles Veg (true) and Non-Veg (false)
    var details: Details? = null,
    var nutrition: Nutrition? = null,
    var imageUrl: String? = null, // URL for the food image
    var videoUrl: String? = null, // URL for the food video
    @ServerTimestamp
    var createdAt: Date? = null,

    var valid: Boolean = true // Changed from isValid to valid
)

/**
 * Represents descriptive details about a food item.
 * Preparation time has been removed as requested.
 *
 * @property description A short, appealing description of the dish.
 * @property ingredients A list of the main ingredients.
 * @property allergens A list of common allergens present in the dish (e.g., "Dairy", "Nuts").
 */
data class Details(
    var description: String? = null,
    var ingredients: List<String>? = null,
    var allergens: List<String>? = null
)

/**
 * Represents the nutritional information for a food item per serving.
 * Contains the specific fields you requested: calories, protein, and carbohydrates.
 *
 * @property servingSize The portion size for which the nutrients are calculated (e.g., "250g").
 * @property calories The energy content in kilocalories (kcal).
 * @property protein The protein content in grams (g).
 * @property carbohydrates The total carbohydrate content in grams (g).
 * @property fat The total fat content in grams (g).
 */
data class Nutrition(
    var servingSize: String? = null,
    var calories: Double? = null,      // Unit: kcal
    var protein: Double? = null,     // Unit: g
    var carbohydrates: Double? = null, // Unit: g
    var fat: Double? = null          // Unit: g
)