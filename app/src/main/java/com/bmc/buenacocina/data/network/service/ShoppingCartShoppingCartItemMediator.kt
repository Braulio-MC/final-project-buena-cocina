package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.dto.CreateShoppingCartDto
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartItemDto
import com.bmc.buenacocina.data.network.dto.UpdateShoppingCartItemDto
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ShoppingCartShoppingCartItemMediator @Inject constructor(
    private val shoppingCartService: ShoppingCartService,
    private val shoppingCartItemService: ShoppingCartItemService
) {
    suspend fun upsert(
        dtoCart: CreateShoppingCartDto,
        dtoItem: CreateShoppingCartItemDto,
        productStoreId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val qCart: (Query) -> Query = { query ->
            query.whereEqualTo("userId", dtoCart.userId)
        }
        val cart = shoppingCartService.get(qCart).firstOrNull()?.firstOrNull()
        if (cart == null) {
            shoppingCartService.create(
                dtoCart,
                onSuccess = { cartId ->
                    shoppingCartItemService.create(
                        cartId,
                        dtoItem,
                        onSuccess,
                        onFailure = {
                            // Rolling back the shopping cart
                            shoppingCartService.delete(
                                cartId,
                                onSuccess = {},
                                onFailure = { message, details ->
                                    onFailure(Exception(details))
                                }
                            )
                        }
                    )
                },
                onFailure = onFailure
            )
        } else {
            if (cart.store.id != productStoreId) {
                onFailure(Exception("Product store does not match shopping cart store"))
            } else {
                val qItem: (Query) -> Query = { query ->
                    query.where(
                        Filter.and(
                            Filter.equalTo("cartId", cart.documentId),
                            Filter.equalTo(FieldPath.of("product", "id"), dtoItem.product.id)
                        )
                    )
                }
                val item = shoppingCartItemService.get(qItem).firstOrNull()?.firstOrNull()
                if (item != null) {
                    val dto = UpdateShoppingCartItemDto(
                        quantity = item.quantity + dtoItem.quantity
                    )
                    shoppingCartItemService.update(
                        cart.documentId,
                        item.documentId,
                        dto,
                        onSuccess,
                        onFailure
                    )
                } else {
                    shoppingCartItemService.create(cart.documentId, dtoItem, onSuccess, onFailure)
                }
            }
        }
    }
}