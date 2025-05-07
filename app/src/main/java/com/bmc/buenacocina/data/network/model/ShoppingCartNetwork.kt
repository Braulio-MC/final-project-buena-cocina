package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class ShoppingCartNetwork (
    @DocumentId
    val documentId: String = "",
    val userId: String = "",
    val store: ShoppingCartStoreNetwork = ShoppingCartStoreNetwork(),
    val itemCount: Int = 0,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    data class ShoppingCartStoreNetwork(
        val id: String = "",
        val ownerId: String = "",
        val name: String = ""
    )
}