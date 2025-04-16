package com.bmc.buenacocina.ui.screen.orderhistory

sealed class OrderHistoryIntent {
    data class UpdateSearchQuery(val searchQuery: String): OrderHistoryIntent()
    data object ClearSearch : OrderHistoryIntent()
    data object Search : OrderHistoryIntent()
}