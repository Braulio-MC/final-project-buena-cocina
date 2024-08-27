package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class OrderNetwork(
    @DocumentId
    val documentId: String = "",
    val status: String = "",
    val user: OrderUserNetwork = OrderUserNetwork(),
    val deliveryLocation: OrderDeliveryLocationNetwork = OrderDeliveryLocationNetwork(),
    val store: OrderStoreNetwork = OrderStoreNetwork(),
    val paymentMethod: OrderPaymentMethodNetwork = OrderPaymentMethodNetwork(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    val paginationKey: String = ""
) {
    data class OrderUserNetwork(
        val id: String = "",
        val name: String = ""
    )

    data class OrderDeliveryLocationNetwork(
        val id: String = "",
        val name: String = ""
    )

    data class OrderStoreNetwork(
        val id: String = "",
        val name: String = ""
    )

    data class OrderPaymentMethodNetwork(
        val id: String = "",
        val name: String = ""
    )
}