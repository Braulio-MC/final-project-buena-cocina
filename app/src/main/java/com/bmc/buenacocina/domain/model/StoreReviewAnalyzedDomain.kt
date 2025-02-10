package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

// From PyApi
data class StoreReviewAnalyzedDomain(
    val id: String,
    val storeId: String,
    val userId: String,
    val rating: Float,
    val comment: String,
    val sentiment: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
