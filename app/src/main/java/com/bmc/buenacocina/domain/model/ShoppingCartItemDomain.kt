package com.bmc.buenacocina.domain.model

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

data class ShoppingCartItemDomain(
    val id: String,
    val product: ShoppingCartItemProductDomain,
    val quantity: BigInteger,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    data class ShoppingCartItemProductDomain(
        val id: String,
        val name: String,
        val description: String,
        val image: String,
        val price: BigDecimal,
        val discount: ShoppingCartItemProductDiscountDomain
    ) {
        data class ShoppingCartItemProductDiscountDomain(
            val id: String,
            val percentage: BigDecimal,
            val startDate: LocalDateTime?,
            val endDate: LocalDateTime?
        )
    }
}