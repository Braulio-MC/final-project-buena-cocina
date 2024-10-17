package com.bmc.buenacocina.domain.model

data class UpsertStoreReviewDomain(
    val id: String? = null,
    val userId: String = "",
    val storeId: String = "",
    val rating: Float = 0f,
    val comment: String = ""
)