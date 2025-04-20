package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.model.InsightTopLocationResultNetwork
import com.bmc.buenacocina.data.network.model.InsightTopRatedStoreResultNetwork
import com.bmc.buenacocina.data.network.model.InsightTopSoldProductResultNetwork
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

// From PyApi
interface InsightService {
    @GET("/insights/get-top-locations-on-map")
    suspend fun getTopLocationsOnMap(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ApiResponse<InsightTopLocationResultNetwork>

    @GET("/insights/get-top-sold-products")
    suspend fun getTopSoldProducts(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ApiResponse<InsightTopSoldProductResultNetwork>

    @GET("/insights/get-top-rated-stores")
    suspend fun getTopRatedStores(
        @Query("start") start: Float? = null,
        @Query("end") end: Float? = null
    ): ApiResponse<InsightTopRatedStoreResultNetwork>
}