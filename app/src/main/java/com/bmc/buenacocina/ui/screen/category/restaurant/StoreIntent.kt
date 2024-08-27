package com.bmc.buenacocina.ui.screen.category.restaurant

sealed class StoreIntent {
    data object getFavoriteStores : StoreIntent()
    data object getBestRatedStores : StoreIntent()
    data object getExploreStores : StoreIntent()
}