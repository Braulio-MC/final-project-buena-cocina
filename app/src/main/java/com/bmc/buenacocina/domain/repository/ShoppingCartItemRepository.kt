package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartItemDto
import com.bmc.buenacocina.data.network.dto.UpdateShoppingCartItemDto
import com.bmc.buenacocina.data.network.service.ShoppingCartItemService
import com.bmc.buenacocina.data.paging.ShoppingCartItemPagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingCartItemRepository @Inject constructor(
    private val shoppingCartItemService: ShoppingCartItemService,
    private val firestore: FirebaseFirestore
) {
    fun create(
        cartId: String,
        dto: CreateShoppingCartItemDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        shoppingCartItemService.create(cartId, dto, onSuccess, onFailure)
    }

    fun update(
        cartId: String,
        itemId: String,
        dto: UpdateShoppingCartItemDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        shoppingCartItemService.update(cartId, itemId, dto, onSuccess, onFailure)
    }

    fun delete(
        cartId: String,
        itemId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        shoppingCartItemService.delete(cartId, itemId, onSuccess, onFailure)
    }

    fun get(cartId: String, itemId: String): Flow<ShoppingCartItemDomain?> {
        val shoppingCartItem = shoppingCartItemService.get(cartId, itemId)
        return shoppingCartItem.map { it?.asDomain() }
    }

    fun get(cartId: String): Flow<List<ShoppingCartItemDomain>> {
        val shoppingCartItems = shoppingCartItemService.get(cartId)
        return shoppingCartItems.map { list -> list.map { it.asDomain() } }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ShoppingCartItemDomain>> {
        val shoppingCartItems = shoppingCartItemService.get(query)
        return shoppingCartItems.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<ShoppingCartItemDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { ShoppingCartItemPagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}