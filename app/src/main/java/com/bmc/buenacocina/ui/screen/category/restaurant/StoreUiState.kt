package com.bmc.buenacocina.ui.screen.category.restaurant

import com.bmc.buenacocina.domain.model.StoreDomain

data class StoreUiState(
    val favoritesStores: List<StoreDomain> = emptyList(),
    val bestRatedStores: List<StoreDomain> = emptyList(),
    val exploreStores: List<StoreDomain> = emptyList()
)