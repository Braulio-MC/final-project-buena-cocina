package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class OrderDomain(
    val id: String,
    val status: String,
    val user: OrderUserDomain,
    val deliveryLocation: OrderDeliveryLocationDomain,
    val store: OrderStoreDomain,
    val paymentMethod: OrderPaymentMethodDomain,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    data class OrderUserDomain(
        val id: String,
        val name: String
    )

    data class OrderDeliveryLocationDomain(
        val id: String,
        val name: String
    )

    data class OrderStoreDomain(
        val id: String,
        val name: String
    )

    data class OrderPaymentMethodDomain(
        val id: String,
        val name: String
    )
}