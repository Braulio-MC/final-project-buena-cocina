package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.data.network.dto.UpdateOrderDto
import com.bmc.buenacocina.domain.repository.OrderRepository
import javax.inject.Inject

class UpdateOrder @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        id: String,
        rated: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dto = UpdateOrderDto(
            rated = rated
        )
        orderRepository.update(
            id = id,
            dto = dto,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}