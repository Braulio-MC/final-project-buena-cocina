package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.PRODUCT_REVIEW_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateProductReviewDto
import com.bmc.buenacocina.data.network.dto.UpsertProductReviewDto
import com.bmc.buenacocina.data.network.model.ProductReviewNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ProductReviewService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateProductReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(PRODUCT_REVIEW_COLLECTION_NAME).document()
        val new = hashMapOf(
            "userId" to dto.userId,
            "productId" to dto.productId,
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

    fun createAsBatch(
        list: List<CreateProductReviewDto>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = firestore.batch()
        list.forEach { dto ->
            val docRef = firestore.collection(PRODUCT_REVIEW_COLLECTION_NAME).document()
            val new = hashMapOf(
                "userId" to dto.userId,
                "productId" to dto.productId,
                "rating" to dto.rating,
                "comment" to dto.comment,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
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

    fun upsertAsBatch(
        list: List<UpsertProductReviewDto>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = firestore.batch()
        list.forEach { dto ->
            val upsert = hashMapOf(
                "rating" to dto.rating,
                "comment" to dto.comment,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val docRef = if (dto.id == null) {
                upsert["userId"] = dto.userId
                upsert["productId"] = dto.productId
                upsert["createdAt"] = FieldValue.serverTimestamp()
                firestore.collection(PRODUCT_REVIEW_COLLECTION_NAME).document()
            } else {
                firestore.collection(PRODUCT_REVIEW_COLLECTION_NAME).document(dto.id)
            }
            batch.set(docRef, upsert, SetOptions.merge())
        }
        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun get(id: String): Flow<ProductReviewNetwork?> = callbackFlow {
        val docRef = firestore.collection(PRODUCT_REVIEW_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val data = snapshot.toObject(ProductReviewNetwork::class.java)
                trySend(data)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ProductReviewNetwork>> = callbackFlow {
        val ref = firestore.collection(PRODUCT_REVIEW_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val data = snapshot.toObjects(ProductReviewNetwork::class.java)
                trySend(data)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}