package com.app.k2t.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.k2t.firebase.model.OrderItem
import com.app.k2t.firebase.repositoryimpl.OrderItemRepositoryImpl
import com.app.k2t.firebase.repositoryimpl.OrderRepositoryImpl
import com.app.k2t.firebase.utils.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class OrderItemViewModel : ViewModel(), KoinComponent {
    private val orderItemRepository: OrderItemRepositoryImpl by inject()
    private val orderRepository: OrderRepositoryImpl by inject()
    private val userViewModel : UserViewModel by inject()

    private val _allOrderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val allOrderItems: StateFlow<List<OrderItem>> = _allOrderItems.asStateFlow()

    private val _orderItemsByOrderId = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItemsByOrderId: MutableStateFlow<List<OrderItem>> = _orderItemsByOrderId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _recentOrderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val recentOrderItems: StateFlow<List<OrderItem>> = _recentOrderItems.asStateFlow()

    // Flow for pending order items, not assigned to any chef yet
    val pendingOrderItems: StateFlow<List<OrderItem>> = recentOrderItems.map { items ->
        items.filter { it.statusCode == OrderStatus.PENDING.code }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow for items accepted by the current chef (combine user and items)
    val acceptedOrderItems: StateFlow<List<OrderItem>> = combine(recentOrderItems, userViewModel.userState) { items, user ->
        items.filter { it.chefId == user?.id && (it.statusCode == OrderStatus.PREPARING.code || it.statusCode == OrderStatus.COMPLETED.code) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        getAllOrderItems()
        getRecentOrderItems()
    }

    fun getAllOrderItems() {
        viewModelScope.launch {
            _isLoading.value = true
            orderItemRepository.getAllOrderItems().collect { items ->
                _allOrderItems.value = items
                _isLoading.value = false
            }
        }
    }
    fun getOrderItemsByOrderId(orderId: String){
        viewModelScope.launch {
            val items = orderItemRepository.getAllOrderItemsByOrderId(orderId)
            _orderItemsByOrderId.value = items
        }
    }
    fun createOrderItem(orderItem: OrderItem) {
        viewModelScope.launch {
            orderItemRepository.createOrderItem(orderItem)
        }
    }
    fun updateOrderItemStatus(orderItemId : String, newStatus : Int) {
        viewModelScope.launch {
            orderItemRepository.updateOrderItem(orderItemId, newStatus)

            // After updating, check if the parent order can be marked as completed
            val item = _allOrderItems.value.find { it.itemId == orderItemId }
            if (newStatus == OrderStatus.COMPLETED.code && item?.orderId != null) {
                checkAndCompleteOrder(item.orderId!!)
            }
        }
    }

    private suspend fun checkAndCompleteOrder(orderId: String) {
        val allItemsForOrder = orderItemRepository.getAllOrderItemsByOrderId(orderId)
        if (allItemsForOrder.isNotEmpty() && allItemsForOrder.all { it.statusCode == OrderStatus.COMPLETED.code}) {
            orderRepository.updateOrderStatus(orderId, OrderStatus.COMPLETED.code)
        }
    }

    fun deleteOrderItem(orderItem: OrderItem) {
        viewModelScope.launch {
            orderItemRepository.deleteOrderItem(orderItem)
        }
    }
    fun getOrderItemById(id: String, onResult: (OrderItem?) -> Unit) {
        viewModelScope.launch {
            val item = orderItemRepository.getOrderItemById(id)
            onResult(item)
        }
    }

    fun updateOrderItemWithOrderId(itemId: String, orderId: String) {
        viewModelScope.launch {
            orderItemRepository.updateOrderItemWithOrderId(itemId, orderId)
        }
    }

    fun acceptOrderItem(itemId: String) {
        viewModelScope.launch {
            val chefId = userViewModel.userState.value?.id
            if (chefId != null) {
                orderItemRepository.acceptOrderItem(itemId, chefId)
            }
        }
    }

    fun getRecentOrderItems() {
        viewModelScope.launch {
            orderItemRepository.getRecentOrderItems().collect { items ->
                _recentOrderItems.value = items
            }
        }
    }
}