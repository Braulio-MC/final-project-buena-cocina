package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

// From PyApi
data class ProductReviewAnalyzedDomain(
    val id: String,
    val productId: String,
    val userId: String,
    val rating: Float,
    val comment: String,
    val sentiment: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
