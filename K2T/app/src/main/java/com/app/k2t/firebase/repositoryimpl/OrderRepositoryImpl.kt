package com.app.k2t.firebase.repositoryimpl

import com.app.k2t.firebase.model.Order
import com.app.k2t.firebase.repository.OrderRepository
import com.app.k2t.firebase.utils.DocumentName
import com.app.k2t.firebase.utils.OrderStatus
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Implementation of [OrderRepository] that interacts with Firebase Firestore.
 *
 * @property firestore The [FirebaseFirestore] instance to use for database operations.
 */
class OrderRepositoryImpl(firestore: FirebaseFirestore) : OrderRepository {

    private val ordersCollection = firestore.collection(DocumentName.ORDER)

    override suspend fun createOrder(order: Order): Task<String> {
        // Firestore will generate the ID for the order document
        return ordersCollection.add(order).continueWith { task ->
            if (task.isSuccessful) {
                task.result?.id ?: throw Exception("Failed to get document ID")
            } else {
                throw task.exception ?: Exception("Unknown error adding order")
            }
        }
    }

    override suspend fun getAllOrders(): Task<QuerySnapshot> {
        return ordersCollection
            .orderBy("orderTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
    }

    override suspend fun getActiveOrdersForTable(tableId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .whereEqualTo("tableID", tableId)
            .whereEqualTo("statusCode", OrderStatus.IN_PROGRESS.code)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { document ->
                        document.toObject(Order::class.java)?.apply { orderId = document.id }
                    }
                    trySend(orders)
                }
            }
        awaitClose { listener.remove() }
    }
    override suspend fun updateOrderStatus(id : String , status : Int ) : Task<Void>{
            return ordersCollection
                .document(id)
                .update("statusCode", status)
    }

//    override fun getOrderById(orderId: String): Task<DocumentSnapshot> {
//        return ordersCollection
//    }
}