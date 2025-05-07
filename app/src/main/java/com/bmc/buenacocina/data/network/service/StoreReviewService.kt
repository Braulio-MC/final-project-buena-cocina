package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.STORE_REVIEW_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateStoreReviewDto
import com.bmc.buenacocina.data.network.dto.UpdateStoreReviewDto
import com.bmc.buenacocina.data.network.dto.UpsertStoreReviewDto
import com.bmc.buenacocina.data.network.model.StoreReviewNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class StoreReviewService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateStoreReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(STORE_REVIEW_COLLECTION_NAME).document()
        val new = hashMapOf(
            "id" to docRef.id,
            "userId" to dto.userId,
            "storeId" to dto.storeId,
            "rating" to dto.rating,
            "comment" to dto.comment,
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

    fun update(
        id: String,
        dto: UpdateStoreReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(STORE_REVIEW_COLLECTION_NAME).document(id)
        val update = hashMapOf(
            "rating" to dto.rating,
            "comment" to dto.comment,
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

    fun upsert(
        dto: UpsertStoreReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val upsert = hashMapOf(
            "rating" to dto.rating,
            "comment" to dto.comment,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        val docRef = if (dto.id == null) {
            upsert["userId"] = dto.userId
            upsert["storeId"] = dto.storeId
            upsert["createdAt"] = FieldValue.serverTimestamp()
            firestore.collection(STORE_REVIEW_COLLECTION_NAME).document()
        } else {
            firestore.collection(STORE_REVIEW_COLLECTION_NAME).document(dto.id)
        }
        docRef.set(upsert, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun get(id: String): Flow<StoreReviewNetwork?> = callbackFlow {
        val docRef = firestore.collection(STORE_REVIEW_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val review = snapshot.toObject(StoreReviewNetwork::class.java)
                trySend(review)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<StoreReviewNetwork>> = callbackFlow {
        val ref = firestore.collection(STORE_REVIEW_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val reviews = snapshot.toObjects(StoreReviewNetwork::class.java)
                trySend(reviews)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}