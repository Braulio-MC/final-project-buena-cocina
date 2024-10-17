package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.API_URL
import com.bmc.buenacocina.data.network.dto.GetGetStreamTokenDto
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GetStreamTokenService {
    @POST("${API_URL}/get-stream-token")
    suspend fun request(
        @Body request: GetGetStreamTokenDto
    ): ApiResponse<String>
}