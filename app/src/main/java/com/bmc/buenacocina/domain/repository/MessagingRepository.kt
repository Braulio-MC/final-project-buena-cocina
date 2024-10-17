package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.service.MessagingService
import com.bmc.buenacocina.domain.mapper.asNetwork
import com.bmc.buenacocina.domain.model.NotificationDomain
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
        notification: NotificationDomain,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dto = notification.asNetwork()
        messagingService.sendMessageToTopic(topic, dto, onSuccess, onFailure)
    }

    fun sendMessageToUserDevices(
        userId: String,
        notification: NotificationDomain,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dto = notification.asNetwork()
        messagingService.sendMessageToUserDevices(userId, dto, onSuccess, onFailure)
    }
}