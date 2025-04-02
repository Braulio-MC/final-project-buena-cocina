package com.bmc.buenacocina.domain.model

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

// From PyApi
data class InsightTopSoldProductDomain(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val categoryName: String,
    val storeName: String,
    val storeOwnerId: String,
    val discountPercentage: BigDecimal,
    val discountStartDate: LocalDateTime,
    val discountEndDate: LocalDateTime,
    val rating: BigDecimal,
    val totalReviews: BigInteger,
    val totalQuantitySold: BigInteger,
    val hitsOnOrders: BigInteger
)
