package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.STORE_COLLECTION_NAME
import com.bmc.buenacocina.data.network.model.StoreNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class StoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun get(id: String): Flow<StoreNetwork?> = callbackFlow {
        val docRef = firestore.collection(STORE_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val store = snapshot.toObject(StoreNetwork::class.java)
                trySend(store)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<StoreNetwork>> = callbackFlow {
        val ref = firestore.collection(STORE_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val stores = snapshot.toObjects(StoreNetwork::class.java)
                trySend(stores)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}