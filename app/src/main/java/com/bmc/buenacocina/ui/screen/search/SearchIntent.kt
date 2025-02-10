package com.bmc.buenacocina.ui.screen.search

sealed class SearchIntent {
    data class UpdateSearchQuery(val searchQuery: String): SearchIntent()
    data object Search : SearchIntent()
}