package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.data.network.dto.UpdateShoppingCartItemDto
import com.bmc.buenacocina.domain.repository.ShoppingCartItemRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigInteger
import javax.inject.Inject

class UpdateShoppingCartItemCount @Inject constructor(
    private val shoppingCartItemRepository: ShoppingCartItemRepository
) {
    private val mutex = Mutex()

    suspend operator fun invoke(
        shoppingCartId: String,
        itemId: String,
        count: BigInteger,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = mutex.withLock {
        val item = shoppingCartItemRepository.get(shoppingCartId, itemId).firstOrNull()
        if (item != null) {
            val finalCount = item.quantity + count
            if (finalCount == BigInteger.ZERO) {
                shoppingCartItemRepository.delete(
                    shoppingCartId,
                    itemId,
                    onSuccess,
                    onFailure
                )
            } else {
                val dto = UpdateShoppingCartItemDto(
                    quantity = finalCount.toInt()
                )
                shoppingCartItemRepository.update(
                    shoppingCartId,
                    itemId,
                    dto,
                    onSuccess,
                    onFailure
                )
            }
        }
    }
}