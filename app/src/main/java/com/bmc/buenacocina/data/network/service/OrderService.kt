package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.ORDER_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateOrderDto
import com.bmc.buenacocina.data.network.dto.UpdateOrderDto
import com.bmc.buenacocina.data.network.model.OrderNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class OrderService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    fun create(
        dto: CreateOrderDto,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        val docRef = firestore.collection(ORDER_COLLECTION_NAME).document()
        val new = hashMapOf(
            "id" to docRef.id,
            "status" to dto.status,
            "rated" to dto.rated,
            "user" to hashMapOf(
                "id" to dto.user.id,
                "name" to dto.user.name
            ),
            "deliveryLocation" to dto.deliveryLocation,
            "store" to hashMapOf(
                "id" to dto.store.id,
                "ownerId" to dto.store.ownerId,
                "name" to dto.store.name
            ),
            "paymentMethod" to hashMapOf(
                "id" to dto.paymentMethod.id,
                "name" to dto.paymentMethod.name
            ),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(new)
            .addOnSuccessListener {
                onSuccess(docRef.id)
            }
            .addOnFailureListener { _ ->
                onFailure("Order create error", "Unknown error")
            }
    }

    fun update(
        id: String,
        dto: UpdateOrderDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(ORDER_COLLECTION_NAME).document(id)
        val update = hashMapOf(
            "rated" to dto.rated,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(update)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun delete(
        id: String,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        val fParams = hashMapOf(
            "collectionName" to ORDER_COLLECTION_NAME,
            "documentId" to id
        )
        functions
            .getHttpsCallable("recursiveCollectionDelete")
            .call(fParams)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<*, *>
                    when {
                        result == null -> {
                            onFailure("Unknown error", "Server did not return a response")
                        }

                        else -> {
                            val message = result["message"] as? String ?: "Successfully deleted"
                            onSuccess(message)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseFunctionsException) {
                        val message = exception.message ?: "Order delete error"
                        val details = exception.details?.toString() ?: ""
                        onFailure(message, details)
                    } else {
                        onFailure("Unexpected error", "Unknown error")
                    }
                }
            }
    }

    fun get(id: String): Flow<OrderNetwork?> = callbackFlow {
        val docRef = firestore.collection(ORDER_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val order = snapshot.toObject(OrderNetwork::class.java)
                trySend(order)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<OrderNetwork>> = callbackFlow {
        val ref = firestore.collection(ORDER_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val orders = snapshot.toObjects(OrderNetwork::class.java)
                trySend(orders)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}
