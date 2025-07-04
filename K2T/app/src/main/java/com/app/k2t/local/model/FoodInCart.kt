package com.app.k2t.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class FoodInCart(
    @PrimaryKey
    val foodId: String,
    val foodName : String,
    var quantity: Int? = null,
    var unitPrice: Double? = null,
    var totalPrice: Double? = null,
    var imageUrl: String? = null
)

