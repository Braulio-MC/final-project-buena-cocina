package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.OrderStatus
import com.bmc.buenacocina.data.network.dto.CreateOrderDto
import com.bmc.buenacocina.data.network.dto.CreateOrderLineDto
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.domain.repository.ShoppingCartRepository
import javax.inject.Inject

class CreateOrder @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shoppingCartRepository: ShoppingCartRepository
) {
    operator fun invoke(
        userId: String,
        userName: String,
        deliveryLocationId: String,
        deliveryLocationName: String,
        storeId: String,
        storeOwnerId: String,
        storeName: String,
        paymentMethodId: String,
        paymentMethodName: String,
        shoppingCartId: String,
        items: List<ShoppingCartItemDomain>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val orderDto = makeCreateOrderDto(
            userId = userId,
            userName = userName,
            deliveryLocationId = deliveryLocationId,
            deliveryLocationName = deliveryLocationName,
            storeId = storeId,
            storeOwnerId = storeOwnerId,
            storeName = storeName,
            paymentMethodId = paymentMethodId,
            paymentMethodName = paymentMethodName
        )
        val orderLineDtoList = makeCreateOrderLineDtoList(items)
        orderRepository.create(
            dto = orderDto,
            lines = orderLineDtoList,
            onSuccess = { orderId ->
                shoppingCartRepository.delete(
                    shoppingCartId,
                    onSuccess,
                    onFailure = { e ->
                        // Rollback order creation
                        orderRepository.delete(
                            orderId,
                            onSuccess = {},
                            onFailure
                        )
                        onFailure(e)
                    }
                )
            },
            onFailure
        )
    }

    private fun makeCreateOrderDto(
        userId: String,
        userName: String,
        deliveryLocationId: String,
        deliveryLocationName: String,
        storeId: String,
        storeOwnerId: String,
        storeName: String,
        paymentMethodId: String,
        paymentMethodName: String,
    ): CreateOrderDto {
        return CreateOrderDto(
            status = OrderStatus.CREATED.status,
            rated = false,
            user = CreateOrderDto.CreateOrderUserDto(
                id = userId,
                name = userName
            ),
            deliveryLocation = CreateOrderDto.CreateOrderDeliveryLocationDto(
                id = deliveryLocationId,
                name = deliveryLocationName
            ),
            store = CreateOrderDto.CreateOrderStoreDto(
                id = storeId,
                ownerId = storeOwnerId,
                name = storeName
            ),
            paymentMethod = CreateOrderDto.CreateOrderPaymentMethodDto(
                id = paymentMethodId,
                name = paymentMethodName
            )
        )
    }

    private fun makeCreateOrderLineDtoList(items: List<ShoppingCartItemDomain>): List<CreateOrderLineDto> {
        return items.map { item ->
            if (item.product.discount.startDate == null || item.product.discount.endDate == null) {
                throw Exception("Failed to create order lines, startDate and endDate are null") // Custom exception here
            }
            CreateOrderLineDto(
                quantity = item.quantity.toInt(),
                product = CreateOrderLineDto.CreateOrderLineProductDto(
                    id = item.product.id,
                    name = item.product.name,
                    description = item.product.description,
                    image = item.product.image,
                    price = item.product.price.toDouble(),
                    discount = CreateOrderLineDto.CreateOrderLineProductDto.CreateOrderLineProductDiscountDto(
                        id = item.product.discount.id,
                        percentage = item.product.discount.percentage.toDouble(),
                        startDate = DateUtils.localDateTimeToFirebaseTimestamp(item.product.discount.startDate),
                        endDate = DateUtils.localDateTimeToFirebaseTimestamp(item.product.discount.endDate)
                    )
                )
            )
        }
    }
}