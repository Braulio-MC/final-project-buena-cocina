package com.bmc.buenacocina.data.network.dto

data class CreateProductReviewDto(
    val userId: String,
    val productId: String,
    val rating: Float,
    val comment: String
)