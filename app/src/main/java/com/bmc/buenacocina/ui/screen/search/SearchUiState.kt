package com.bmc.buenacocina.ui.screen.search

import androidx.paging.PagingData
import com.bmc.buenacocina.domain.model.SearchResultDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SearchUiState(
    val searchQuery: String = "",
    val isActive: Boolean = false,
    val hits: Flow<PagingData<SearchResultDomain>> = emptyFlow()
)