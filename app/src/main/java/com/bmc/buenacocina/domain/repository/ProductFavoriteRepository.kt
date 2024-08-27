package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateProductFavoriteDto
import com.bmc.buenacocina.data.network.service.ProductFavoriteService
import com.bmc.buenacocina.data.paging.ProductFavoritePagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.ProductFavoriteDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductFavoriteRepository @Inject constructor(
    private val productFavoriteService: ProductFavoriteService,
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateProductFavoriteDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        productFavoriteService.create(dto, onSuccess, onFailure)
    }

    fun delete(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        productFavoriteService.delete(id, onSuccess, onFailure)
    }

    fun get(id: String): Flow<ProductFavoriteDomain?> {
        val productFavorite = productFavoriteService.get(id)
        return productFavorite.map { it?.asDomain() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ProductFavoriteDomain>> {
        val productFavorites = productFavoriteService.get(query)
        return productFavorites.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<ProductFavoriteDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { ProductFavoritePagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}