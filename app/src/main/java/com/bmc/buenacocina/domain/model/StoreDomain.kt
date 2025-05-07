package com.bmc.buenacocina.domain.model

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

data class StoreDomain(
    val id: String,
    val name: String,
    val description: String,
    val email: String,
    val phoneNumber: String,
    val startTime: StoreWorkingHoursDomain,
    val endTime: StoreWorkingHoursDomain,
    val rating: BigDecimal,
    val totalRating: BigDecimal,
    val totalReviews: BigInteger,
    val image: String,
    val userId: String,
    val updatedAt: LocalDateTime?,
    val createdAt: LocalDateTime?
) {
    data class StoreWorkingHoursDomain(
        val hour: Int,
        val minute: Int
    )
}