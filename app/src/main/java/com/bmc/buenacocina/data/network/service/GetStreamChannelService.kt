package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.API_URL
import com.bmc.buenacocina.data.network.dto.CreateGetStreamChannelDto
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GetStreamChannelService {
    @POST("${API_URL}/channels")
    suspend fun create(
        @Body dto: CreateGetStreamChannelDto
    ): ApiResponse<String>
}