package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.dto.CreateMessageDto
import com.bmc.buenacocina.data.network.service.MessagingService
import javax.inject.Inject

class MessagingRepository @Inject constructor(
    private val messagingService: MessagingService
) {
    fun createTopic(
        topic: String,
        userId: String,
        storeId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        messagingService.createTopic(topic, userId, storeId, onSuccess, onFailure)
    }

    fun sendMessageToTopic(
        topic: String,
        dto: CreateMessageDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        messagingService.sendMessageToTopic(topic, dto, onSuccess, onFailure)
    }
}