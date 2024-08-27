package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.PAYMENT_METHOD_COLLECTION_NAME
import com.bmc.buenacocina.data.network.model.PaymentMethodNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PaymentMethodService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun get(id: String): Flow<PaymentMethodNetwork?> = callbackFlow {
        val docRef = firestore.collection(PAYMENT_METHOD_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val paymentMethod = snapshot.toObject(PaymentMethodNetwork::class.java)
                trySend(paymentMethod)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<PaymentMethodNetwork>> = callbackFlow {
        val ref = firestore.collection(PAYMENT_METHOD_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val paymentMethods = snapshot.toObjects(PaymentMethodNetwork::class.java)
                trySend(paymentMethods)
            } else {
                trySend(emptyList())

            }
        }
        awaitClose { listener.remove() }
    }
}