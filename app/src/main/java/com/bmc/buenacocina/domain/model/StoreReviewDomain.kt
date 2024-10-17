package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class StoreReviewDomain(
    val id: String = "",
    val userId: String = "",
    val storeId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
