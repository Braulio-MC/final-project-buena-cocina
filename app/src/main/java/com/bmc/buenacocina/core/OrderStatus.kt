package com.bmc.buenacocina.core

enum class OrderStatus(val status: String) {
    CREATED("Creado"),
    ACTIVE("Activo"),
    PROGRESS("En progreso"),
    ON_WAY("En camino"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado"),
    ERROR("Error"),
    UNASSIGNED("")
}