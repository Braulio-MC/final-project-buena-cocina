package com.bmc.buenacocina.data.network.dto

data class CreateOrderDto (
    val status: String,
    val user: CreateOrderUserDto,
    val deliveryLocation: CreateOrderDeliveryLocationDto,
    val store: CreateOrderStoreDto,
    val paymentMethod: CreateOrderPaymentMethodDto,
) {
    data class CreateOrderUserDto(
        val id: String,
        val name: String
    )

    data class CreateOrderDeliveryLocationDto(
        val id: String,
        val name: String
    )

    data class CreateOrderStoreDto(
        val id: String,
        val name: String
    )

    data class CreateOrderPaymentMethodDto(
        val id: String,
        val name: String
    )
}