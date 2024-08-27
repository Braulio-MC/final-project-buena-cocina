package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class PaymentMethodNetwork(
    @DocumentId
    val documentId: String = "",
    val name: String = "",
    val description: String = "",
    val paginationKey: String = "",
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
)