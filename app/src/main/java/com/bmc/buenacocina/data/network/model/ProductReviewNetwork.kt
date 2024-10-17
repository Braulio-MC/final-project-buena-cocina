package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class ProductReviewNetwork(
    @DocumentId
    val documentId: String = "",
    val userId: String = "",
    val productId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
)
