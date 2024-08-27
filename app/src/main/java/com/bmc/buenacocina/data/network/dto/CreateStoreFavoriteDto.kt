package com.bmc.buenacocina.data.network.dto

data class CreateStoreFavoriteDto(
    val name: String,
    val description: String,
    val image: String,
    val phoneNumber: String,
    val email: String,
    val storeId: String,
    val userId: String
)