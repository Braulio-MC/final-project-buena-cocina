package com.bmc.buenacocina.domain.model

data class NotificationDomain(
    val notification: InnerNotificationDomain,
    val data: HashMap<String, String>
) {
    data class InnerNotificationDomain(
        val title: String,
        val body: String,
    )
}