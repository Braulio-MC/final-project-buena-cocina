package com.bmc.buenacocina.data.network.model

import io.getstream.chat.android.models.User

data class GetStreamUserCredentials(
    val apiKey: String,
    val user: User,
    val token: String
)