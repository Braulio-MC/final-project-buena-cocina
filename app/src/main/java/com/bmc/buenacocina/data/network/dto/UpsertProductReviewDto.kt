package com.bmc.buenacocina.data.network.dto

data class UpsertProductReviewDto(
    val id: String? = null,
    val userId: String,
    val productId: String,
    val rating: Float,
    val comment: String
)
