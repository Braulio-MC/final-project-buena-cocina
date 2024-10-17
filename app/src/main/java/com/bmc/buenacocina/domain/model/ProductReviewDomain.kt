package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class ProductReviewDomain(
    val id: String,
    val userId: String,
    val productId: String,
    val rating: Float,
    val comment: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
