package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

// From PyApi
data class InsightTopRatedStoreResultNetwork(
    @SerializedName("data") val stores: List<InsightTopRatedStoreNetwork>
)
