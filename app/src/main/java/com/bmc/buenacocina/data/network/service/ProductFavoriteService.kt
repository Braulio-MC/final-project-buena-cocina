package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.PRODUCT_FAVORITE_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateProductFavoriteDto
import com.bmc.buenacocina.data.network.model.ProductFavoriteNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ProductFavoriteService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateProductFavoriteDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(PRODUCT_FAVORITE_COLLECTION_NAME).document()
        val new = hashMapOf(
            "id" to docRef.id,
            "userId" to dto.userId,
            "productId" to dto.productId,
            "productStoreOwnerId" to dto.productStoreOwnerId,
            "productName" to dto.productName,
            "productImage" to dto.productImage,
            "productDescription" to dto.productDescription,
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
        val docRef = firestore.collection(PRODUCT_FAVORITE_COLLECTION_NAME).document(id)
        docRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun get(id: String): Flow<ProductFavoriteNetwork?> = callbackFlow {
        val docRef = firestore.collection(PRODUCT_FAVORITE_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val productFavorite = snapshot.toObject(ProductFavoriteNetwork::class.java)
                trySend(productFavorite)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ProductFavoriteNetwork>> = callbackFlow {
        val ref = firestore.collection(PRODUCT_FAVORITE_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val productFavorites = snapshot.toObjects(ProductFavoriteNetwork::class.java)
                trySend(productFavorites)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}