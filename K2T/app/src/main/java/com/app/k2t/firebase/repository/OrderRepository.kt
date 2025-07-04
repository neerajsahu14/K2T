package com.app.k2t.firebase.repository


import com.app.k2t.firebase.model.Order
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Interface defining the contract for order data operations.
 * This repository provides methods to create, retrieve, and update orders
 * in a persistent data store, likely a database like Firestore.
 */
interface OrderRepository {

    /**
     * Creates a new order in the database.
     * @param order The Order object to save. The orderId should typically be null for add operations.
     * @return A Task that completes with the ID of the newly created order.
     */
    suspend fun createOrder(order: Order): Task<String> // Or Task<DocumentReference> if you need the ref immediately

    /**
     * Gets all orders.
     * @return A Task that provides a QuerySnapshot containing all order documents.
     */
    suspend fun getAllOrders(): Task<QuerySnapshot>

    /**
     * Gets a specific order by its ID.
     * @param orderId The ID of the order to fetch.
     * @return A Task that provides a DocumentSnapshot for the order.
     */
//     fun getOrderById(orderId: String): Task<DocumentSnapshot> // Optional, but useful

    suspend fun getActiveOrdersForTable(tableId: String): Flow<List<Order>>
    suspend fun updateOrderStatus(id : String , status : Int ) : Task<Void>

}