package com.bmc.buenacocina.data.network.dto

data class CreateStoreReviewDto(
    val userId: String,
    val storeId: String,
    val rating: Float,
    val comment: String
)