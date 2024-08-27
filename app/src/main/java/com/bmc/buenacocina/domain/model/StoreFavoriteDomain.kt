package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class StoreFavoriteDomain(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val phoneNumber: String,
    val email: String,
    val storeId: String,
    val userId: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
