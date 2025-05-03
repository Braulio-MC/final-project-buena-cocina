package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.dto.AlgoliaGetSecuredSearchApiKeyDto
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AlgoliaTokenService {
    @POST("/get-secured-search-Key")
    suspend fun requestScopedToken(
        @Header("Authorization") authorization: String,
        @Body request: AlgoliaGetSecuredSearchApiKeyDto
    ): ApiResponse<String>
}