package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.ORDER_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateOrderDto
import com.bmc.buenacocina.data.network.dto.UpdateOrderDto
import com.bmc.buenacocina.data.network.model.OrderNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject

class OrderService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    fun create(
        dto: CreateOrderDto,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
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
            "paginationKey" to UUID.randomUUID().toString(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(new)
            .addOnSuccessListener {
                onSuccess(docRef.id)
            }
            .addOnFailureListener { e ->
                onFailure(e)
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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fParams = hashMapOf(
            "collectionName" to ORDER_COLLECTION_NAME,
            "documentId" to id
        )
        functions
            .getHttpsCallable("recursiveCollectionDelete")
            .call(fParams)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
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
