package com.bmc.buenacocina.data.network.dto

import com.google.firebase.Timestamp

data class CreateOrderLineDto(
    val quantity: Int,
    val product: CreateOrderLineProductDto
) {
    data class CreateOrderLineProductDto(
        val id: String,
        val name: String,
        val description: String,
        val image: String,
        val price: Double,
        val discount: CreateOrderLineProductDiscountDto
    ) {
        data class CreateOrderLineProductDiscountDto(
            val id: String,
            val percentage: Double,
            val startDate: Timestamp,
            val endDate: Timestamp
        )
    }
}