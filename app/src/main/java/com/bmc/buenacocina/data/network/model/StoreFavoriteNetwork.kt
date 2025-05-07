package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class StoreFavoriteNetwork(
    @DocumentId
    val documentId: String = "",
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val storeId: String = "",
    val userId: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
