package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class InsightTopRatedStoreNetwork(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("avg_rating") val rating: Float,
    @SerializedName("total_reviews") val totalReviews: Int
)
