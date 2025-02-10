package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class StoreReviewAnalyzedResultNetwork(
    @SerializedName("query_id") val queryId: String,
    @SerializedName("reviews") val reviews: List<StoreReviewAnalyzedNetwork>,
    @SerializedName("pagination") val pagination: StoreReviewAnalyzedResultPaginationNetwork
) {
    data class StoreReviewAnalyzedResultPaginationNetwork(
        @SerializedName("next_cursor") val next: String?
    )
}
