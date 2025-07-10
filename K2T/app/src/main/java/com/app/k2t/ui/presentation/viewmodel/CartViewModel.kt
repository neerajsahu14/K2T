package com.app.k2t.ui.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.k2t.firebase.model.Food
import com.app.k2t.local.model.FoodInCart
import com.app.k2t.local.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class CartViewModel : ViewModel(), KoinComponent {
    private val cartRepository: CartRepository by inject()
    private val _allFoodInCart = MutableStateFlow<List<FoodInCart>>(emptyList())
    val allFoodInCart: StateFlow<List<FoodInCart>> =  _allFoodInCart.asStateFlow()

    fun insertFood(foodInCart: FoodInCart) {
        viewModelScope.launch {
            cartRepository.insertFood(foodInCart)
        }
    }
    fun getAllFoodInCart() {
        viewModelScope.launch {
            cartRepository.getAllFoodInCart().collect {
                _allFoodInCart.value = it
            }
        }
    }
    init {
        viewModelScope.launch {
            getAllFoodInCart()
        }
    }
    fun updateFood(foodInCart: FoodInCart) {
        viewModelScope.launch {
            val updatedFood = foodInCart.copy(
                totalPrice = (foodInCart.unitPrice ?: 0.0) * (foodInCart.quantity ?: 1)
            )
            cartRepository.updateFood(updatedFood)
        }
    }

    fun deleteFood(foodInCart: FoodInCart) {
        viewModelScope.launch {
            cartRepository.deleteFood(foodInCart)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
    fun isFoodInCart(foodId: String): Flow<Boolean> {
        return cartRepository.isFoodInCart(foodId)
    }
}