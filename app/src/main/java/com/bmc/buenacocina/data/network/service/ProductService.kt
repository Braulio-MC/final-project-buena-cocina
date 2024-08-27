package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.PRODUCT_COLLECTION_NAME
import com.bmc.buenacocina.data.network.model.ProductNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ProductService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun get(id: String): Flow<ProductNetwork?> = callbackFlow {
        val docRef = firestore.collection(PRODUCT_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val product = snapshot.toObject(ProductNetwork::class.java)
                trySend(product)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ProductNetwork>> = callbackFlow {
        val ref = firestore.collection(PRODUCT_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val products = snapshot.toObjects(ProductNetwork::class.java)
                trySend(products)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}