package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class StoreNetwork(
    @DocumentId
    val documentId: String = "",
    val name: String = "",
    val description: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val rating: Double = 0.0,
    val totalRating: Double = 0.0,
    val totalReviews: Int = 0,
    val image: String = "",
    val userId: String = "",
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val paginationKey: String = ""
)