package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateProductReviewDto
import com.bmc.buenacocina.data.network.dto.UpsertProductReviewDto
import com.bmc.buenacocina.data.network.service.ProductReviewService
import com.bmc.buenacocina.data.paging.ProductReviewPagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.ProductReviewDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductReviewRepository @Inject constructor(
    private val productReviewService: ProductReviewService,
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateProductReviewDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        productReviewService.create(dto, onSuccess, onFailure)
    }

    fun createAsBatch(
        dto: List<CreateProductReviewDto>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        productReviewService.createAsBatch(dto, onSuccess, onFailure)
    }

    fun upsertAsBatch(
        dto: List<UpsertProductReviewDto>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        productReviewService.upsertAsBatch(dto, onSuccess, onFailure)
    }

    fun get(id: String): Flow<ProductReviewDomain?> {
        val review = productReviewService.get(id)
        return review.map { it?.asDomain() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<ProductReviewDomain>> {
        val reviews = productReviewService.get(query)
        return reviews.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<ProductReviewDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { ProductReviewPagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}