package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.STORE_FAVORITE_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateStoreFavoriteDto
import com.bmc.buenacocina.data.network.model.StoreFavoriteNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class StoreFavoriteService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateStoreFavoriteDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(STORE_FAVORITE_COLLECTION_NAME).document()
        val new = hashMapOf(
            "id" to docRef.id,
            "name" to dto.name,
            "description" to dto.description,
            "image" to dto.image,
            "phoneNumber" to dto.phoneNumber,
            "email" to dto.email,
            "storeId" to dto.storeId,
            "userId" to dto.userId,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(new)
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
        val docRef = firestore.collection(STORE_FAVORITE_COLLECTION_NAME).document(id)
        docRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun get(id: String): Flow<StoreFavoriteNetwork?> = callbackFlow {
        val docRef = firestore.collection(STORE_FAVORITE_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val storeFavorite = snapshot.toObject(StoreFavoriteNetwork::class.java)
                trySend(storeFavorite)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<StoreFavoriteNetwork>> = callbackFlow {
        val ref = firestore.collection(STORE_FAVORITE_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val storeFavorites = snapshot.toObjects(StoreFavoriteNetwork::class.java)
                trySend(storeFavorites)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}