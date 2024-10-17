package com.bmc.buenacocina.domain.model

data class UpsertProductReviewDomain(
    val id: String? = null,
    val userId: String = "",
    val productId: String = "",
    val rating: Float = 0f,
    val comment: String = ""
)