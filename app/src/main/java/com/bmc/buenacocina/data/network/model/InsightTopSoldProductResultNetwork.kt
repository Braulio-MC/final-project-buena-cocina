package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class InsightTopSoldProductResultNetwork(
    @SerializedName("data") val products: List<InsightTopSoldProductNetwork>
)
