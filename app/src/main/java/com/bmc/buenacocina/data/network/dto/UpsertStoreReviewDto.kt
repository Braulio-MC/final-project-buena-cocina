package com.bmc.buenacocina.data.network.dto

data class UpsertStoreReviewDto(
    val id: String? = null,
    val userId: String,
    val storeId: String,
    val rating: Float,
    val comment: String,
)