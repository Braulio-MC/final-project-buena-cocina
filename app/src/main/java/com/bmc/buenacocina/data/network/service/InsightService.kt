package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.model.InsightTopLocationResultNetwork
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
}