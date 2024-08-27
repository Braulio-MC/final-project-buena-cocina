package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class PaymentMethodDomain(
    val id: String,
    val name: String,
    val description: String,
    val updatedAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
)