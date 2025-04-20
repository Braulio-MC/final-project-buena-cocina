package com.bmc.buenacocina.ui.screen.category.restaurant

import com.bmc.buenacocina.domain.model.InsightTopRatedStoreDomain
import com.bmc.buenacocina.domain.model.RemoteConfigProductCategoryDomain

data class StoreUiState(
    val isLoadingProductCategories: Boolean = false,
    val isLoadingTopRatedStores: Boolean = false,
    val productCategories: List<RemoteConfigProductCategoryDomain> = emptyList(),
    val selectedProductCategory: RemoteConfigProductCategoryDomain? = null,
    val topRatedStores: List<InsightTopRatedStoreDomain> = emptyList()
)
