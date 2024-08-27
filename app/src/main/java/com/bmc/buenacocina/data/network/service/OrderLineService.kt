package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.ORDER_COLLECTION_NAME
import com.bmc.buenacocina.core.ORDER_SUB_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateOrderLineDto
import com.bmc.buenacocina.data.network.model.OrderLineNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject

class OrderLineService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun create(
        orderId: String,
        dto: CreateOrderLineDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore
            .collection(ORDER_COLLECTION_NAME)
            .document(orderId)
            .collection(ORDER_SUB_COLLECTION_NAME)
            .document()
        val new = hashMapOf(
            "id" to docRef.id,
            "orderId" to orderId,
            "quantity" to dto.quantity,
            "product" to hashMapOf(
                "id" to dto.product.id,
                "name" to dto.product.name,
                "description" to dto.product.description,
                "image" to dto.product.image,
                "price" to dto.product.price,
                "discount" to hashMapOf(
                    "id" to dto.product.discount.id,
                    "percentage" to dto.product.discount.percentage,
                    "startDate" to dto.product.discount.startDate,
                    "endDate" to dto.product.discount.endDate
                )
            ),
            "paginationKey" to UUID.randomUUID().toString(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        docRef.set(new)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun createAsBatch(
        orderId: String,
        list: List<CreateOrderLineDto>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = firestore.batch()
        list.forEach { dto ->
            val docRef = firestore
                .collection(ORDER_COLLECTION_NAME)
                .document(orderId)
                .collection(ORDER_SUB_COLLECTION_NAME)
                .document()
            val new = hashMapOf(
                "id" to docRef.id,
                "orderId" to orderId,
                "quantity" to dto.quantity,
                "product" to hashMapOf(
                    "id" to dto.product.id,
                    "name" to dto.product.name,
                    "description" to dto.product.description,
                    "image" to dto.product.image,
                    "price" to dto.product.price,
                    "discount" to hashMapOf(
                        "id" to dto.product.discount.id,
                        "percentage" to dto.product.discount.percentage,
                        "startDate" to dto.product.discount.startDate,
                        "endDate" to dto.product.discount.endDate
                    )
                ),
                "paginationKey" to UUID.randomUUID().toString(),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp(),
            )
            batch.set(docRef, new)
        }
        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun get(orderId: String, lineId: String): Flow<OrderLineNetwork?> = callbackFlow {
        val docRef = firestore
            .collection(ORDER_COLLECTION_NAME)
            .document(orderId)
            .collection(ORDER_SUB_COLLECTION_NAME)
            .document(lineId)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val orderLine = snapshot.toObject(OrderLineNetwork::class.java)
                trySend(orderLine)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(orderId: String): Flow<List<OrderLineNetwork>> = callbackFlow {
        val ref = firestore
            .collection(ORDER_COLLECTION_NAME)
            .document(orderId)
            .collection(ORDER_SUB_COLLECTION_NAME)
        val listener = ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val orderLines = snapshot.toObjects(OrderLineNetwork::class.java)
                trySend(orderLines)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<OrderLineNetwork>> = callbackFlow {
        val ref = firestore.collectionGroup(ORDER_SUB_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val orderLines = snapshot.toObjects(OrderLineNetwork::class.java)
                trySend(orderLines)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}