package com.bmc.buenacocina.data.network.dto

data class CreateShoppingCartDto (
    val userId: String,
    val storeId: String,
    val storeOwnerId: String,
    val storeName: String,
)