package com.bmc.buenacocina.data.network.dto

import com.google.firebase.Timestamp

data class CreateShoppingCartItemDto(
    val quantity: Int,
    val product: CreateShoppingCartItemProductDto
) {
    data class CreateShoppingCartItemProductDto(
        val id: String,
        val name: String,
        val description: String,
        val image: String,
        val price: Double,
        val discount: CreateShoppingCartItemProductDiscountDto
    ) {
        data class CreateShoppingCartItemProductDiscountDto(
            val id: String,
            val percentage: Double,
            val startDate: Timestamp,
            val endDate: Timestamp
        )
    }
}