package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp

data class OrderNetwork(
    @DocumentId
    val documentId: String = "",
    val status: String = "",
    val rated: Boolean = false,
    val user: OrderUserNetwork = OrderUserNetwork(),
    val deliveryLocation: GeoPoint? = null,
    val store: OrderStoreNetwork = OrderStoreNetwork(),
    val paymentMethod: OrderPaymentMethodNetwork = OrderPaymentMethodNetwork(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    data class OrderUserNetwork(
        val id: String = "",
        val name: String = ""
    )

    data class OrderStoreNetwork(
        val id: String = "",
        val ownerId: String = "",
        val name: String = ""
    )

    data class OrderPaymentMethodNetwork(
        val id: String = "",
        val name: String = ""
    )
}