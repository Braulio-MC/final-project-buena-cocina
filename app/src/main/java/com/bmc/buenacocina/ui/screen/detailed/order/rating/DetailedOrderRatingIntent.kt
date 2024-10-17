package com.bmc.buenacocina.ui.screen.detailed.order.rating

sealed class DetailedOrderRatingIntent {
    data class StoreRatingChanged(val rating: Float): DetailedOrderRatingIntent()
    data class StoreCommentChanged(val comment: String): DetailedOrderRatingIntent()
    data class ItemRatingChanged(val id: String, val rating: Float): DetailedOrderRatingIntent()
    data class ItemCommentChanged(val id: String, val comment: String): DetailedOrderRatingIntent()
    data object Submit: DetailedOrderRatingIntent()
}