package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateStoreFavoriteDto
import com.bmc.buenacocina.data.network.service.StoreFavoriteService
import com.bmc.buenacocina.data.paging.StoreFavoritePagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.StoreFavoriteDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreFavoriteRepository @Inject constructor(
    private val storeFavoriteService: StoreFavoriteService,
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateStoreFavoriteDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        storeFavoriteService.create(dto, onSuccess, onFailure)
    }

    fun delete(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
       storeFavoriteService.delete(id, onSuccess, onFailure)
    }

    fun get(id: String): Flow<StoreFavoriteDomain?> {
        val storeFavorite = storeFavoriteService.get(id)
        return storeFavorite.map { it?.asDomain() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<StoreFavoriteDomain>> {
        val storeFavorites = storeFavoriteService.get(query)
        return storeFavorites.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<StoreFavoriteDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { StoreFavoritePagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}