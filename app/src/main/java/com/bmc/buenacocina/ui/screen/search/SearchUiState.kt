package com.bmc.buenacocina.ui.screen.search

import com.bmc.buenacocina.common.Searchable

data class SearchUiState(
    val isLoadingHits: Boolean = false,
    val searchQuery: String = "",
    val hits: List<Searchable> = emptyList()
)