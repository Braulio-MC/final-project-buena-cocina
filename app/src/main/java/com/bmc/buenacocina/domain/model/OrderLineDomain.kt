package com.bmc.buenacocina.domain.model

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

data class OrderLineDomain(
    val id: String,
    val quantity: BigInteger,
    val product: OrderLineProductDomain,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    data class OrderLineProductDomain(
        val id: String,
        val name: String,
        val description: String,
        val image: String,
        val price: BigDecimal,
        val discount: OrderLineProductDiscountDomain
    ) {
        data class OrderLineProductDiscountDomain(
            val id: String,
            val percentage: BigDecimal,
            val startDate: LocalDateTime?,
            val endDate: LocalDateTime?
        )
    }
}