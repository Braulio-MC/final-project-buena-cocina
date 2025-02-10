package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.model.ProductReviewAnalyzedResultNetwork
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// From PyApi
interface ProductReviewAnalyzedService {
    @GET("/product-reviews/product/{product_id}")
    suspend fun pagingByProductIdWithRange(
        @Path("product_id") productId: String,
        @Query("limit") limit: Int? = null,
        @Query("next_cursor") nextCursor: String? = null,
        @Query("start_date") start: String? = null, // ISO-8601 formatted date
        @Query("end_date") end: String? = null // ISO-8601 formatted date
    ): ApiResponse<ProductReviewAnalyzedResultNetwork>

    @GET("/product-reviews/user/{user_id}")
    suspend fun pagingByUserIdWithRange(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int? = null,
        @Query("next_cursor") nextCursor: String? = null,
        @Query("start_date") start: String? = null, // ISO-8601 formatted date
        @Query("end_date") end: String? = null // ISO-8601 formatted date
    ): ApiResponse<ProductReviewAnalyzedResultNetwork>
}