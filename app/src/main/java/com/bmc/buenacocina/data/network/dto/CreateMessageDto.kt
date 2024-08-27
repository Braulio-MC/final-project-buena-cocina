package com.bmc.buenacocina.data.network.dto

data class CreateMessageDto(
    val notification: CreateMessageNotificationDto,
    val data: HashMap<String, String>
) {
    data class CreateMessageNotificationDto(
        val title: String,
        val body: String,
    )
}