package com.bmc.buenacocina.data.network.dto

data class CreateProductFavoriteDto (
    val userId: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val productDescription: String,
    val productStoreOwnerId: String
)