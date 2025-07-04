package com.app.k2t.local.repository

import androidx.lifecycle.LiveData
import com.app.k2t.local.dao.CartDao
import com.app.k2t.local.model.FoodInCart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class CartRepository(private val cartDao: CartDao) {

    suspend fun getAllFoodInCart(): Flow<List<FoodInCart>> {
        return cartDao.getAllFoodInCart()
    }

    suspend fun insertFood(foodInCart: FoodInCart) {
        cartDao.insertFood(foodInCart)
    }

    suspend fun updateFood(foodInCart: FoodInCart) {
        cartDao.updateFood(foodInCart)
    }

    suspend fun deleteFood(foodInCart: FoodInCart) {
        cartDao.deleteFood(foodInCart)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    fun isFoodInCart(foodId: String): Flow<Boolean> {
        return cartDao.isFoodInCart(foodId)
    }
}
