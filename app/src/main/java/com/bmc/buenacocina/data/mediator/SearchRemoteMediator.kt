package com.bmc.buenacocina.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bmc.buenacocina.data.local.LocalDatabase
import com.bmc.buenacocina.data.local.dao.SearchDao
import com.bmc.buenacocina.data.local.dao.SearchRemoteKeyDao
import com.bmc.buenacocina.data.local.model.SearchRemoteKeyEntity
import com.bmc.buenacocina.data.local.model.SearchResultEntity
import com.bmc.buenacocina.data.network.service.SearchService
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

//@OptIn(ExperimentalPagingApi::class)
//class SearchRemoteMediator @Inject constructor(
//    private val query: String,
//    private val localDatabase: LocalDatabase,
//    private val apiService: SearchService,
//    private val searchDao: SearchDao,
//    private val remoteKeyDao: SearchRemoteKeyDao
//) : RemoteMediator<Int, SearchResultEntity>() {
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, SearchResultEntity>
//    ): MediatorResult {
//        return try {
//            val loadKey = when (loadType) {
//                LoadType.REFRESH -> 0
//                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> {
//                    val remoteKey = localDatabase.withTransaction {
//                        remoteKeyDao.find(query)
//                    }
//                    if (remoteKey.nextKey == null) {
//                        return MediatorResult.Success(endOfPaginationReached = true)
//                    }
//                    remoteKey.nextKey
//                }
//            }
//            val response = apiService.search(
//                query = query,
//                page = loadKey,
//                perPage = state.config.pageSize
//            )
//            var endOfPagination = false
//            response.suspendOnSuccess {
//                localDatabase.withTransaction {
//                    if (loadType == LoadType.REFRESH) {
//                        remoteKeyDao.delete(query)
//                        searchDao.delete()
//                    }
//                    if (data.pagination.currentPage < data.pagination.nbPages) {
//                        val nextPage = data.pagination.currentPage + 1
//                        remoteKeyDao.insertOrReplace(SearchRemoteKeyEntity(query, nextPage))
//                    } else {
//                        endOfPagination = true
//                        remoteKeyDao.insertOrReplace(SearchRemoteKeyEntity(query, null))
//                    }
//                    searchDao.insert(*data.data.map { it.asEntity() }.toTypedArray())
//                }
//            }.onException {
//                throw throwable
//            }
//            MediatorResult.Success(endOfPaginationReached = endOfPagination)
//        } catch (e: IOException) {
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            MediatorResult.Error(e)
//        }
//    }
//}