package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.LOCATION_COLLECTION_NAME
import com.bmc.buenacocina.data.network.model.LocationNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun get(id: String): Flow<LocationNetwork?> = callbackFlow {
        val docRef = firestore.collection(LOCATION_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val location = snapshot.toObject(LocationNetwork::class.java)
                trySend(location)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<LocationNetwork>> = callbackFlow {
        val ref = firestore.collection(LOCATION_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val locations = snapshot.toObjects(LocationNetwork::class.java)
                trySend(locations)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}