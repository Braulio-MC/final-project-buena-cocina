package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.core.GetStreamChannelTypes
import com.bmc.buenacocina.data.network.dto.CreateGetStreamChannelDto
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.bmc.buenacocina.domain.repository.GetStreamChannelRepository
import javax.inject.Inject

class CreateGetStreamChannel @Inject constructor(
    private val channelRepository: GetStreamChannelRepository
) {
    suspend operator fun invoke(
        orderId: String,
        storeOwnerId: String,
        storeName: String,
        userId: String,
        userName: String,
    ) : Result<String, DataError> {
        val shortOrderId = orderId.takeLast(5)
        val channelName = "$shortOrderId - $storeName/$userName"
        val getStreamStoreOwnerId = storeOwnerId.replace("|", "-")
        val getStreamUserId = userId.replace("|", "-")
        val dto = CreateGetStreamChannelDto(
            type = GetStreamChannelTypes.MESSAGING.type,
            id = orderId,
            options = CreateGetStreamChannelDto.CreateGetStreamChannelOptionsDto(
                name = channelName,
                blocked = false,
                members = listOf(getStreamStoreOwnerId, getStreamUserId)
            )
        )
        return channelRepository.create(dto)
    }
}