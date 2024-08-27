package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.model.SearchResultNetwork
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("/search")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): ApiResponse<SearchResultNetwork>
}