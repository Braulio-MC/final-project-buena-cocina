package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.BASE_API_URL
import com.bmc.buenacocina.data.network.dto.AlgoliaGetSecuredSearchApiKeyDto
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AlgoliaTokenService {
    @POST("${BASE_API_URL}/get-secured-search-Key")
    suspend fun requestScopedToken(
        @Header("Authorization") authorization: String,
        @Body request: AlgoliaGetSecuredSearchApiKeyDto
    ): ApiResponse<String>
}