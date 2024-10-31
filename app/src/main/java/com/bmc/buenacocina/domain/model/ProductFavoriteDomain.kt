package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class ProductFavoriteDomain (
    val id: String,
    val userId: String,
    val productId: String,
    val productStoreOwnerId : String,
    val productName: String,
    val productImage: String,
    val productDescription: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)