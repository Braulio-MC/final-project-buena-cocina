package com.bmc.buenacocina.data.network.service

import retrofit2.http.GET
import retrofit2.http.Query
import com.bmc.buenacocina.data.network.model.ChatBotApiResponse



// Interfaz de Retrofit para la API
interface BotApiService {
    @GET("/api/query")
    suspend fun getResponse(@Query("query") userQuery: String): ChatBotApiResponse
}
