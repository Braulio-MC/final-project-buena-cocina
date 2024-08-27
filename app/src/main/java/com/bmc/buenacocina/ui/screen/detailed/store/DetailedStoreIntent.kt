package com.bmc.buenacocina.ui.screen.detailed.store

sealed class DetailedStoreIntent {
    data object ToggleFavoriteStore : DetailedStoreIntent()
}