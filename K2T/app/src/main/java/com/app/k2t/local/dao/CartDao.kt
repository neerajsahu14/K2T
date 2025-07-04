package com.app.k2t.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.k2t.local.model.FoodInCart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(foodInCart: FoodInCart)

    @Update
    suspend fun updateFood(foodInCart: FoodInCart)

    @Delete
    suspend fun deleteFood(foodInCart: FoodInCart)

    @Query("DELETE FROM cart_table")
    suspend fun clearCart()

    @Query("SELECT * FROM cart_table")
    fun getAllFoodInCart(): Flow<List<FoodInCart>>

    @Query("SELECT EXISTS(SELECT 1 FROM cart_table WHERE foodId = :foodId)")
    fun isFoodInCart(foodId: String): Flow<Boolean>

}
