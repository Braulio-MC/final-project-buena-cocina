package com.bmc.buenacocina.ui.screen.detailed.order

sealed class DetailedOrderIntent {
    data object CreateChannel : DetailedOrderIntent()
}