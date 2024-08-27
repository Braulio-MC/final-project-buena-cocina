package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.SHOPPING_CART_COLLECTION_NAME
import com.bmc.buenacocina.core.SHOPPING_CART_SUB_COLLECTION_NAME
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartItemDto
import com.bmc.buenacocina.data.network.dto.UpdateShoppingCartItemDto
import com.bmc.buenacocina.data.network.model.ShoppingCartItemNetwork
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject

class ShoppingCartItemService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun create(
        cartId: String,
        dto: CreateShoppingCartItemDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore
            .collection(SHOPPING_CART_COLLECTION_NAME)
            .document(cartId)
            .collection(SHOPPING_CART_SUB_COLLECTION_NAME)
            .document()
        val new = hashMapOf(
            "id" to docRef.id,
            "cartId" to cartId,
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

    fun update(
        cartId: String,
        itemId: String,
        dto: UpdateShoppingCartItemDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore
            .collection(SHOPPING_CART_COLLECTION_NAME)
            .document(cartId)
            .collection(SHOPPING_CART_SUB_COLLECTION_NAME)
            .document(itemId)
        val update = hashMapOf(
            "quantity" to dto.quantity,
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
        cartId: String,
        itemId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = firestore
            .collection(SHOPPING_CART_COLLECTION_NAME)
            .document(cartId)
            .collection(SHOPPING_CART_SUB_COLLECTION_NAME)
            .document(itemId)
        docRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun get(cartId: String, itemId: String): Flow<ShoppingCartItemNetwork?> = callbackFlow {
        val docRef = firestore
            .collection(SHOPPING_CART_COLLECTION_NAME)
            .document(cartId)
            .collection(SHOPPING_CART_SUB_COLLECTION_NAME)
            .document(itemId)
        val listener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                val shoppingCartItem = snapshot.toObject(ShoppingCartItemNetwork::class.java)
                trySend(shoppingCartItem)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(cartId: String): Flow<List<ShoppingCartItemNetwork>> = callbackFlow {
        val ref = firestore
            .collection(SHOPPING_CART_COLLECTION_NAME)
            .document(cartId)
            .collection(SHOPPING_CART_SUB_COLLECTION_NAME)
        val listener = ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val shoppingCartItems = snapshot.toObjects(ShoppingCartItemNetwork::class.java)
                trySend(shoppingCartItems)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ShoppingCartItemNetwork>> = callbackFlow {
        val ref = firestore.collectionGroup(SHOPPING_CART_SUB_COLLECTION_NAME)
        val q = query(ref)
        val listener = q.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else if (snapshot != null && !snapshot.isEmpty) {
                val shoppingCartItems = snapshot.toObjects(ShoppingCartItemNetwork::class.java)
                trySend(shoppingCartItems)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }
}