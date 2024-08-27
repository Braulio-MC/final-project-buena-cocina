package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartDto
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartItemDto
import com.bmc.buenacocina.data.network.dto.UpdateShoppingCartDto
import com.bmc.buenacocina.data.network.dto.UpdateShoppingCartItemDto
import com.bmc.buenacocina.data.network.service.ShoppingCartItemService
import com.bmc.buenacocina.data.network.service.ShoppingCartService
import com.bmc.buenacocina.data.paging.ShoppingCartPagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.ShoppingCartDomain
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingCartRepository @Inject constructor(
    private val shoppingCartService: ShoppingCartService,
    private val shoppingCartItemService: ShoppingCartItemService,
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateShoppingCartDto,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        shoppingCartService.create(dto, onSuccess, onFailure)
    }

    suspend fun upsertItem(
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
                    shoppingCartItemService.create(cartId, dtoItem, onSuccess, onFailure)
                },
                onFailure = onFailure
            )
        } else {
            if (cart.store.id != productStoreId) {
                onFailure(Exception("Store id does not match"))
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

    fun update(
        id: String,
        dto: UpdateShoppingCartDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        shoppingCartService.update(id, dto, onSuccess, onFailure)
    }

    fun delete(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        shoppingCartService.delete(id, onSuccess, onFailure)
    }

    fun get(id: String): Flow<ShoppingCartDomain?> {
        val cart = shoppingCartService.get(id)
        return cart.map { it?.asDomain() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ShoppingCartDomain>> {
        val carts = shoppingCartService.get(query)
        return carts.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<ShoppingCartDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { ShoppingCartPagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}