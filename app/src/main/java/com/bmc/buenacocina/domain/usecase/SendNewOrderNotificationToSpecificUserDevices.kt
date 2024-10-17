package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.model.NotificationDomain
import com.bmc.buenacocina.domain.repository.MessagingRepository
import javax.inject.Inject

class SendNewOrderNotificationToSpecificUserDevices @Inject constructor(
    private val messagingRepository: MessagingRepository
) {
    operator fun invoke(
        storeId: String,
        storeName: String,
        userName: String,
        locationName: String,
        itemCount: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val notification = buildNotification(storeName, userName, locationName, itemCount)
        messagingRepository.sendMessageToUserDevices(
            storeId,
            notification,
            onSuccess,
            onFailure
        )
    }

    private fun buildNotification(
        storeName: String,
        userName: String,
        locationName: String,
        itemCount: Int
    ): NotificationDomain {
        return NotificationDomain(
            notification = NotificationDomain.InnerNotificationDomain(
                title = "Una nueva orden ha sido creada para $storeName",
                body = "$userName ha creado una nueva orden con $itemCount productos para entregarse en $locationName"
            ),
            data = hashMapOf()
        )
    }
}