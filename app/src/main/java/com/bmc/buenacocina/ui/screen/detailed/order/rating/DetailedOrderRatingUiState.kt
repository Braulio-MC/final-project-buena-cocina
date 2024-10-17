package com.bmc.buenacocina.ui.screen.detailed.order.rating

import com.bmc.buenacocina.domain.UiText
import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.model.OrderLineDomain
import com.bmc.buenacocina.domain.model.UpsertProductReviewDomain
import com.bmc.buenacocina.domain.model.UpsertStoreReviewDomain

data class DetailedOrderRatingUiResultState(
    val isWaitingForStoreRatingResult: Boolean = false,
    val isWaitingForItemRatingsResult: Boolean = false,
    val isWaitingForOverallRatingResult: Boolean = false,
    val storeRating: UpsertStoreReviewDomain = UpsertStoreReviewDomain(),
    val storeRatingError: UiText? = null,
    val storeCommentError: UiText? = null,
    val itemRatings: List<DetailedOrderRatingUiStateItemRating> = emptyList(),
) {
    data class DetailedOrderRatingUiStateItemRating(
        val item: UpsertProductReviewDomain = UpsertProductReviewDomain(),
        val ratingError: UiText? = null,
        val commentError: UiText? = null,
    )
}

data class DetailedOrderRatingUiState(
    val isLoading: Boolean = false,
    val order: OrderDomain? = null,
    val lines: List<OrderLineDomain> = emptyList(),
)
