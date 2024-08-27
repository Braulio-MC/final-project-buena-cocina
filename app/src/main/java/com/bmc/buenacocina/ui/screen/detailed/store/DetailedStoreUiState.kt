package com.bmc.buenacocina.ui.screen.detailed.store

import com.bmc.buenacocina.domain.model.StoreDomain
import com.bmc.buenacocina.domain.model.StoreFavoriteDomain

data class DetailedStoreUiResultState(
    val isWaitingForResult: Boolean = false
)

data class DetailedStoreUiState(
    val isLoading: Boolean = false,
    val store: StoreDomain? = null,
    val favorite: StoreFavoriteDomain? = null,
)