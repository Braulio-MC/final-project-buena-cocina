package com.bmc.buenacocina.core

enum class OrderHistorySearchChips(val tag: String) {
    ORDER_STATUS_CREATED(OrderStatus.CREATED.status),
    ORDER_STATUS_ACTIVE(OrderStatus.ACTIVE.status),
    ORDER_STATUS_PROGRESS(OrderStatus.PROGRESS.status),
    ORDER_STATUS_ON_WAY(OrderStatus.ON_WAY.status),
    ORDER_STATUS_DELIVERED(OrderStatus.DELIVERED.status),
    ORDER_RATE_STATUS_FALSE("Sin calificar"),
    ORDER_RATE_STATUS_TRUE("Calificados")
}