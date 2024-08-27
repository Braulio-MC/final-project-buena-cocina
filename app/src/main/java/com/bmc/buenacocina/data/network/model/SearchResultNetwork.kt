package com.bmc.buenacocina.data.network.model

import com.google.gson.annotations.SerializedName

data class SearchResultNetwork(
    @SerializedName("data") val data: List<SearchResultContent>,
    @SerializedName("pagination") val pagination: SearchResultPaginationNetwork
) {
    data class SearchResultContent(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("image") val image: String,
        @SerializedName("type") val type: String,
        @SerializedName("description1") val description1: String,
        @SerializedName("description2") val description2: String
    )

    data class SearchResultPaginationNetwork(
        @SerializedName("currentPage") val currentPage: Int,
        @SerializedName("nbPages") val nbPages: Int,
        @SerializedName("nbHits") val nbHits: Int
    )
}
