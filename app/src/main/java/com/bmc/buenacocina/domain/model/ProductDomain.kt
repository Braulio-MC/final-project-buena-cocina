package com.bmc.buenacocina.domain.model

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

data class ProductDomain(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val price: BigDecimal,
    val updatedAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
    val quantity: BigInteger,
    val discount: ProductDiscountDomain,
    val store: ProductStoreDomain,
    val category: ProductCategoryDomain,
) {
    data class ProductDiscountDomain(
        val id: String,
        val percentage: BigDecimal,
        val startDate: LocalDateTime?,
        val endDate: LocalDateTime?
    )

    data class ProductStoreDomain(
        val id: String,
        val name: String
    )

    data class ProductCategoryDomain(
        val id: String,
        val name: String,
        val parentName: String
    )
}