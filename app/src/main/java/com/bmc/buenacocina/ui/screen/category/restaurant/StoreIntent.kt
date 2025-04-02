package com.bmc.buenacocina.ui.screen.category.restaurant

import com.bmc.buenacocina.domain.model.RemoteConfigProductCategoryDomain

sealed class StoreIntent {
    data class UpdateCurrentProductCategory(val productCategory: RemoteConfigProductCategoryDomain?) :
        StoreIntent()
}