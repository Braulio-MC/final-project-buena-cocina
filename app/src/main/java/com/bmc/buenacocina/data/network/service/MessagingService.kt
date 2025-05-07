package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.dto.CreateNotificationDto
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import javax.inject.Inject

class MessagingService @Inject constructor(
    private val functions: FirebaseFunctions
) {
    fun createTopic(
        topic: String,
        userId: String,
        storeId: String,
        onSuccess: (String, Int) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        val fParams = hashMapOf(
            "topic" to topic,
            "userId" to userId,
            "storeId" to storeId
        )
        functions
            .getHttpsCallable("messaging-createTopic")
            .call(fParams)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<*, *>
                    when {
                        result == null -> {
                            onFailure("Unknown error", "Server did not return a response")
                        }

                        else -> {
                            val message = result["message"] as? String ?: "Successfully created"
                            val response = result["data"] as? Map<*, *>
                            val validTokens = response?.get("validTokens") as? Int ?: 0
                            onSuccess(message, validTokens)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseFunctionsException) {
                        val message = exception.message ?: "Topic create error"
                        val details = exception.details?.toString() ?: ""
                        onFailure(message, details)
                    } else {
                        onFailure("Unexpected error", "Unknown error")
                    }
                }
            }
    }

    fun sendMessageToTopic(
        topic: String,
        dto: CreateNotificationDto,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
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
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<*, *>
                    when {
                        result == null -> {
                            onFailure("Unknown error", "Server did not return a response")
                        }

                        else -> {
                            val message = result["message"] as? String ?: "Successfully sent to topic"
                            onSuccess(message)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseFunctionsException) {
                        val message = exception.message ?: "Sent message error"
                        val details = exception.details?.toString() ?: ""
                        onFailure(message, details)
                    } else {
                        onFailure("Unexpected error", "Unknown error")
                    }
                }
            }
    }

    fun sendMessageToUserDevices(
        userId: String,
        dto: CreateNotificationDto,
        onSuccess: (String, Int) -> Unit,
        onFailure: (String, String) -> Unit
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
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<*, *>
                    when {
                        result == null -> {
                            onFailure("Unknown error", "Server did not return a response")
                        }

                        else -> {
                            val message = result["message"] as? String ?: "Successfully sent"
                            val response = result["data"] as? Map<*, *>
                            val validTokens = response?.get("validTokens") as? Int ?: 0
                            onSuccess(message, validTokens)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseFunctionsException) {
                        val message = exception.message ?: "Sent message error"
                        val details = exception.details?.toString() ?: ""
                        onFailure(message, details)
                    } else {
                        onFailure("Unexpected error", "Unknown error")
                    }
                }
            }
    }
}