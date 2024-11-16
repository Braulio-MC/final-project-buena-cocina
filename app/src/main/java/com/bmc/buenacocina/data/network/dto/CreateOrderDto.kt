package com.bmc.buenacocina.data.network.dto

import com.google.firebase.firestore.GeoPoint

data class CreateOrderDto (
    val status: String,
    val rated: Boolean,
    val user: CreateOrderUserDto,
    val deliveryLocation: GeoPoint,
    val store: CreateOrderStoreDto,
    val paymentMethod: CreateOrderPaymentMethodDto,
) {
    data class CreateOrderUserDto(
        val id: String,
        val name: String
    )

    data class CreateOrderStoreDto(
        val id: String,
        val ownerId: String,
        val name: String
    )

    data class CreateOrderPaymentMethodDto(
        val id: String,
        val name: String
    )
}