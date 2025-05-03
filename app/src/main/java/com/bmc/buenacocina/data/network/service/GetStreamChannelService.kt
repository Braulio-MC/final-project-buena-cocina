package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.dto.CreateGetStreamChannelDto
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GetStreamChannelService {
    @POST("/channels")
    suspend fun create(
        @Body dto: CreateGetStreamChannelDto
    ): ApiResponse<String>
}