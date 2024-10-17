package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateStoreReviewDto
import com.bmc.buenacocina.data.network.dto.UpdateStoreReviewDto
import com.bmc.buenacocina.data.network.dto.UpsertStoreReviewDto
import com.bmc.buenacocina.data.network.service.StoreReviewService
import com.bmc.buenacocina.data.paging.StoreReviewPagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.StoreReviewDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreReviewRepository @Inject constructor(
    private val storeReviewService: StoreReviewService,
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateStoreReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        storeReviewService.create(dto, onSuccess, onFailure)
    }

    fun update(
        id: String,
        dto: UpdateStoreReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        storeReviewService.update(id, dto, onSuccess, onFailure)
    }

    fun upsert(
        dto: UpsertStoreReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        storeReviewService.upsert(dto, onSuccess, onFailure)
    }

    fun get(id: String): Flow<StoreReviewDomain?> {
        val review = storeReviewService.get(id)
        return review.map { it?.asDomain() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<StoreReviewDomain>> {
        val reviews = storeReviewService.get(query)
        return reviews.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<StoreReviewDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { StoreReviewPagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}