package com.bmc.buenacocina.domain.model

import com.google.firebase.firestore.GeoPoint
import java.time.LocalDateTime

data class OrderDomain(
    val id: String,
    val status: String,
    val rated: Boolean,
    val user: OrderUserDomain,
    val deliveryLocation: GeoPoint?,
    val store: OrderStoreDomain,
    val paymentMethod: OrderPaymentMethodDomain,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    data class OrderUserDomain(
        val id: String,
        val name: String
    )

    data class OrderStoreDomain(
        val id: String,
        val ownerId: String,
        val name: String
    )

    data class OrderPaymentMethodDomain(
        val id: String,
        val name: String
    )
}