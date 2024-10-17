package com.bmc.buenacocina.data.network.dto

data class CreateGetStreamChannelDto(
    val type: String,
    val id: String,
    val options: CreateGetStreamChannelOptionsDto
) {
    data class CreateGetStreamChannelOptionsDto(
        val name: String,
        val blocked: Boolean = false,
        val members: List<String>
    )
}