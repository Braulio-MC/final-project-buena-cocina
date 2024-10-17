package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.dto.CreateNotificationDto
import com.google.firebase.functions.FirebaseFunctions
import javax.inject.Inject

class MessagingService @Inject constructor(
    private val functions: FirebaseFunctions
) {
    fun createTopic(
        topic: String,
        userId: String,
        storeId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fParams = hashMapOf(
            "topic" to topic,
            "userId" to userId,
            "storeId" to storeId
        )
        functions
            .getHttpsCallable("messaging-createTopic")
            .call(fParams)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun sendMessageToTopic(
        topic: String,
        dto: CreateNotificationDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fParams = hashMapOf(
            "topic" to topic,
            "message" to hashMapOf(
                "notification" to hashMapOf(
                    "title" to dto.notification.title,
                    "body" to dto.notification.body
                ),
                "data" to dto.data
            )
        )
        functions
            .getHttpsCallable("messaging-sendMessageToTopic")
            .call(fParams)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun sendMessageToUserDevices(
        userId: String,
        dto: CreateNotificationDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fParams = hashMapOf(
            "userId" to userId,
            "message" to hashMapOf(
                "notification" to hashMapOf(
                    "title" to dto.notification.title,
                    "body" to dto.notification.body
                ),
                "data" to dto.data
            )
        )
        functions
            .getHttpsCallable("messaging-sendMessageToUserDevices")
            .call(fParams)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}