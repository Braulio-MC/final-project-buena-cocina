package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class LocationNetwork (
    @DocumentId
    val documentId: String = "",
    val name: String = "",
    val description: String = "",
    val storeId: String = "",
    val paginationKey: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)