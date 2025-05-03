package com.bmc.buenacocina.domain.repository

import android.util.Log
import com.bmc.buenacocina.data.network.model.ChatBotApiResponse
import com.bmc.buenacocina.data.network.service.BotApiService
import javax.inject.Inject

class ChatbotRepository @Inject constructor(
    private val apiService: BotApiService
) {
    suspend fun getChatbotData(question: String): ChatBotApiResponse {
        return try {
            Log.d("ChatbotRepository", "Enviando consulta: $question")
            val response = apiService.getResponse(question)
            Log.d("ChatbotRepository", "Respuesta recibida: $response")

            when (response) {
                is ChatBotApiResponse.ProductResponse -> {
                    if (response.data.isEmpty()) {
                        ChatBotApiResponse.Message(
                            type = "message",
                            message = "No encontré productos relacionados. 😕"
                        )
                    } else {
                        val limitedProducts = response.data.take(3)
                        ChatBotApiResponse.ProductResponse(response.type, limitedProducts)
                    }
                }
                is ChatBotApiResponse.StoreResponse -> {
                    if (response.data.isEmpty()) {
                        ChatBotApiResponse.Message(
                            type = "message",
                            message = "No encontré tiendas relacionadas. 😕"
                        )
                    } else {
                        val limitedStores = response.data.take(3)
                        ChatBotApiResponse.StoreResponse(response.type, limitedStores)
                    }
                }
                is ChatBotApiResponse.Message -> {
                    response
                }
            }
        } catch (e: Exception) {
            Log.e("ChatbotRepository", "Error en la API", e)
            ChatBotApiResponse.Message(
                type = "message",
                message = "Hubo un problema al conectarse con el bot. Inténtalo más tarde. 😞"
            )
        }
    }
}
