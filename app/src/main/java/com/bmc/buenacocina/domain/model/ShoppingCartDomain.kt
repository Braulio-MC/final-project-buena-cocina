package com.bmc.buenacocina.domain.model

import java.time.LocalDateTime

data class ShoppingCartDomain(
    val id: String,
    val userId: String,
    val store: ShoppingCartStoreDomain,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    data class ShoppingCartStoreDomain(
        val id: String,
        val ownerId: String,
        val name: String
    )
}