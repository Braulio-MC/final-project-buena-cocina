package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class LocationDomain(
    val id: String,
    val name: String,
    val description: String,
    val storeId: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)