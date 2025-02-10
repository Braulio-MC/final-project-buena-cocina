package com.bmc.buenacocina.ui.screen.detailed.store

import com.bmc.buenacocina.domain.model.StoreDomain
import com.bmc.buenacocina.domain.model.StoreFavoriteDomain

data class DetailedStoreUiState(
    val isWaitingForFavoriteResult: Boolean = false,
    val isLoadingStore: Boolean = false,
    val isLoadingFavorite: Boolean = false,
    val store: StoreDomain? = null,
    val favorite: StoreFavoriteDomain? = null
)