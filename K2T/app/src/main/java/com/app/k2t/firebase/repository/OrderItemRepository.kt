package com.app.k2t.firebase.repository

import com.app.k2t.firebase.model.OrderItem
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

/**
 * Represents a repository for managing order items.
 */
interface OrderItemRepository {
    suspend fun createOrderItem(orderItem: OrderItem): OrderItem
    suspend fun updateOrderItem(orderItemId: String, newStatus: Int): Task<Void>
    suspend fun deleteOrderItem(orderItem: OrderItem)
    suspend fun getOrderItemById(id: String): OrderItem?
    suspend fun acceptOrderItem(itemId: String, chefId: String): Task<Void>
    suspend fun getAllOrderItemsByOrderId(orderId: String): List<OrderItem>
    suspend fun updateOrderItemWithOrderId(itemId: String, orderId: String): Task<Void>
    suspend fun getAllOrderItems() : Flow<List<OrderItem>>
    fun updateOrderItemWithChefId(itemId: String, chefId: String): Task<Void>
    fun getRecentOrderItems(): Flow<List<OrderItem>>
}