package com.bmc.buenacocina.data.network.model

data class ChatBotMessageNetwork (
    val text: String,
    val isUser: Boolean // true = usuario, false = bot
)