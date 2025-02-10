package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class ProductReviewAnalyzedResultNetwork(
    @SerializedName("query_id") val queryId: String,
    @SerializedName("reviews") val reviews: List<ProductReviewAnalyzedNetwork>,
    @SerializedName("pagination") val pagination: ProductReviewAnalyzedResultPaginationNetwork
) {
    data class ProductReviewAnalyzedResultPaginationNetwork(
        @SerializedName("next_cursor") val next: String?
    )
}
