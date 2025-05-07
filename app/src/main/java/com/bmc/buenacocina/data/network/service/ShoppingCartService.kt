package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.SHOPPING_CART_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartDto
import com.bmc.buenacocina.data.network.dto.UpdateShoppingCartDto
import com.bmc.buenacocina.data.network.model.ShoppingCartNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ShoppingCartService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    fun create(
        dto: CreateShoppingCartDto,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(SHOPPING_CART_COLLECTION_NAME).document()
        val new = hashMapOf(
            "id" to docRef.id,
            "userId" to dto.userId,
            "store" to hashMapOf(
                "id" to dto.storeId,
                "ownerId" to dto.storeOwnerId,
                "name" to dto.storeName
            ),
            "itemCount" to 0,
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
        dto: UpdateShoppingCartDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore.collection(SHOPPING_CART_COLLECTION_NAME).document(id)
        val update = hashMapOf(
            "userId" to dto.userId,
            "store" to hashMapOf(
                "id" to dto.storeId,
                "ownerId" to dto.storeOwnerId,
                "name" to dto.storeName
            ),
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
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        val fParams = hashMapOf(
            "collectionName" to SHOPPING_CART_COLLECTION_NAME,
            "documentId" to id
        )
        functions
            .getHttpsCallable("recursiveCollectionDelete")
            .call(fParams)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<*, *>
                    when {
                        result == null -> {
                            onFailure("Unknown error", "Server did not return a response")
                        }

                        else -> {
                            val message = result["message"] as? String ?: "Successfully deleted"
                            onSuccess(message)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseFunctionsException) {
                        val message = exception.message ?: "Shopping cart delete error"
                        val details = exception.details?.toString() ?: ""
                        onFailure(message, details)
                    } else {
                        onFailure("Unexpected error", "Unknown error")
                    }
                }
            }
    }

    fun get(id: String): Flow<ShoppingCartNetwork?> = callbackFlow {
        val docRef = firestore.collection(SHOPPING_CART_COLLECTION_NAME).document(id)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val shoppingCart = snapshot.toObject(ShoppingCartNetwork::class.java)
                trySend(shoppingCart)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ShoppingCartNetwork>> = callbackFlow {
        val ref = firestore.collection(SHOPPING_CART_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val shoppingCarts = snapshot.toObjects(ShoppingCartNetwork::class.java)
                trySend(shoppingCarts)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}