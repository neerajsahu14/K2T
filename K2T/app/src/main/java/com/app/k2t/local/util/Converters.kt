package com.app.k2t.local.util

import androidx.room.TypeConverter
import com.app.k2t.firebase.model.OrderItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromOrderFoodDetailsList(value: List<OrderItem>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toOrderFoodDetailsList(value: String?): List<OrderItem>? {
        val type = object : TypeToken<List<OrderItem>>() {}.type
        return Gson().fromJson(value, type)
    }
}
