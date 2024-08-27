package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class StoreDomain(
    val id: String,
    val name: String,
    val description: String,
    val email: String,
    val phoneNumber: String,
    val image: String,
    val userId: String,
    val updatedAt: LocalDateTime?,
    val createdAt: LocalDateTime?
)