package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class StoreReviewAnalyzedNetwork(
    @SerializedName("id") val id: String,
    @SerializedName("store_id") val storeId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("comment") val comment: String,
    @SerializedName("sentiment") val sentiment: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
