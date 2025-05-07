package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class InsightTopSoldProductNetwork(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("categories") val categories: List<InsightTopSoldProductCategoryNetwork>,
    @SerializedName("store_name") val storeName: String,
    @SerializedName("store_owner_id") val storeOwnerId: String,
    @SerializedName("discount_percentage") val discountPercentage: Float,
    @SerializedName("discount_start_date") val discountStartDate: String,
    @SerializedName("discount_end_date") val discountEndDate: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("total_reviews") val totalReviews: Int,
    @SerializedName("total_quantity_sold") val totalQuantitySold: Int,
    @SerializedName("hits_on_orders") val hitsOnOrders: Int
) {
    data class InsightTopSoldProductCategoryNetwork(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String
    )
}
