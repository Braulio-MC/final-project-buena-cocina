package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.model.GetStreamUserCredentials
import com.bmc.buenacocina.data.network.service.ChatService
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatService: ChatService
) {
    suspend fun connectUser(credentials: GetStreamUserCredentials?): Result<Unit> {
        return chatService.connectUser(credentials)
    }

    suspend fun disconnectUser() {
        chatService.disconnectUser()
    }
}