package com.app.k2t.firebase.repositoryimpl

import com.app.k2t.firebase.model.OrderItem
import com.app.k2t.firebase.repository.OrderItemRepository
import com.app.k2t.firebase.utils.DocumentName
import com.app.k2t.firebase.utils.OrderStatus
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of [OrderItemRepository] that uses Firebase Firestore as the data source.
 * This class provides methods to interact with the "orderItem" collection in Firestore.
 *
 * @property db An instance of [FirebaseFirestore] used to interact with the database.
 */
class OrderItemRepositoryImpl(private val db: FirebaseFirestore) : OrderItemRepository {

    private val orderItemsCollection = db.collection(DocumentName.ORDER_ITEM)

    override suspend fun createOrderItem(orderItem: OrderItem): OrderItem {
        orderItemsCollection.document(orderItem.itemId.toString()).set(orderItem).await()
        return orderItem
    }

    override suspend fun updateOrderItem(orderItemId: String, newStatus: Int): Task<Void> {
        return orderItemsCollection
           .document(orderItemId)
            .update("statusCode", newStatus)
    }

    override suspend fun acceptOrderItem(itemId: String, chefId: String): Task<Void> {
        return orderItemsCollection
            .document(itemId)
            .update(mapOf(
                "statusCode" to OrderStatus.PREPARING.code,
                "chefId" to chefId
            ))
    }

    override suspend fun deleteOrderItem(orderItem: OrderItem) {
        orderItemsCollection.document(orderItem.itemId.toString()).delete().await()
    }

    override suspend fun getOrderItemById(id: String): OrderItem? {
        val snapshot = orderItemsCollection.document(id).get().await()
        return snapshot.toObject(OrderItem::class.java)
    }

    override suspend fun getAllOrderItemsByOrderId(orderId: String): List<OrderItem> {
        val snapshot = orderItemsCollection.whereEqualTo("orderId", orderId).get().await()
        return snapshot.toObjects(OrderItem::class.java)
    }
    override suspend fun updateOrderItemWithOrderId(itemId: String, orderId: String): Task<Void> {
        return orderItemsCollection
            .document(itemId)
            .update("orderId", orderId)
    }

    override suspend fun getAllOrderItems(): Flow<List<OrderItem>> = kotlinx.coroutines.flow.callbackFlow {
        val listenerRegistration = orderItemsCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val orderItems = snapshot.toObjects(OrderItem::class.java)
                trySend(orderItems)
            }
        }
        awaitClose { listenerRegistration.remove() }
    }

    override fun updateOrderItemWithChefId(
        itemId: String,
        chefId: String
    ): Task<Void> {
        return orderItemsCollection
            .document(itemId)
            .update("chefId", chefId)
    }

    override fun getRecentOrderItems(): Flow<List<OrderItem>> = kotlinx.coroutines.flow.callbackFlow {
        // Calculate midnight today
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val midnight = calendar.time

        val listenerRegistration = orderItemsCollection
            .whereGreaterThanOrEqualTo("addedAt", midnight)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orderItems = snapshot.toObjects(OrderItem::class.java)
                    trySend(orderItems)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}