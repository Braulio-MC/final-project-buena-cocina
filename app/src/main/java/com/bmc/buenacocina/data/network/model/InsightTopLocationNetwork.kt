package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class InsightTopLocationNetwork(
    @SerializedName("geohash") val geohash: String,
    @SerializedName("geopoint") val geopoint: InsightTopLocationGeoPointNetwork,
    @SerializedName("order_count") val intensity: Int
) {
    data class InsightTopLocationGeoPointNetwork(
        @SerializedName("latitude") val lat: Double,
        @SerializedName("longitude") val lng: Double
    )
}
