package com.bmc.buenacocina.data.network.dto

data class CreateNotificationDto(
    val notification: CreateInnerNotificationDto,
    val data: HashMap<String, String>
) {
    data class CreateInnerNotificationDto(
        val title: String,
        val body: String,
    )
}