package com.bmc.buenacocina.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.SEARCH_PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.local.LocalDatabase
import com.bmc.buenacocina.data.local.dao.SearchDao
import com.bmc.buenacocina.data.local.dao.SearchRemoteKeyDao
import com.bmc.buenacocina.data.network.service.SearchService
import com.bmc.buenacocina.di.AppDispatcher
import com.bmc.buenacocina.di.AppDispatchers
import com.bmc.buenacocina.domain.model.SearchResultDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepository @Inject constructor (
    private val remoteService: SearchService,
    private val localDatabase: LocalDatabase,
    private val searchDao: SearchDao,
    private val searchRemoteKeyDao: SearchRemoteKeyDao,
    @AppDispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
//    @OptIn(ExperimentalPagingApi::class)
//    fun search(query: String): Flow<PagingData<SearchResultDomain>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = SEARCH_PAGING_PAGE_SIZE,
//                enablePlaceholders = false
//            ),
//            remoteMediator = SearchRemoteMediator(
//                query = query,
//                localDatabase = localDatabase,
//                apiService = remoteService,
//                searchDao = searchDao,
//                remoteKeyDao = searchRemoteKeyDao
//            ),
//            pagingSourceFactory = { searchDao.get() }
//        ).flow.map { pagingData ->
//            pagingData.map { it.asDomain() }
//        }.flowOn(ioDispatcher)
//    }
}