package com.bmc.buenacocina.data.network.dto

data class UpdateShoppingCartDto (
    val userId: String,
    val storeId: String,
    val storeName: String
)