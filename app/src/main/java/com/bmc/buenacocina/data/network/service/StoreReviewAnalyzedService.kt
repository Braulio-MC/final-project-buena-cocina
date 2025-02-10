package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.model.StoreReviewAnalyzedResultNetwork
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// From PyApi
interface StoreReviewAnalyzedService {
    @GET("/store-reviews/store/{store_id}")
    suspend fun getByStoreIdWithRange(
        @Path("store_id") storeId: String,
        @Query("limit") limit: Int? = null,
        @Query("next_cursor") nextCursor: String? = null,
        @Query("start_date") start: String? = null, // ISO-8601 formatted date
        @Query("end_date") end: String? = null // ISO-8601 formatted date
    ): ApiResponse<StoreReviewAnalyzedResultNetwork>

    @GET("/store-reviews/user/{user_id}")
    suspend fun getByUserIdWithRange(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int? = null,
        @Query("next_cursor") nextCursor: String? = null,
        @Query("start_date") start: String? = null, // ISO-8601 formatted date
        @Query("end_date") end: String? = null // ISO-8601 formatted date
    ): ApiResponse<StoreReviewAnalyzedResultNetwork>
}