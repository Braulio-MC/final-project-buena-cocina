package com.bmc.buenacocina.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.bmc.buenacocina.data.local.model.SearchResultEntity

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg search: SearchResultEntity): LongArray

    @Update
    suspend fun update(vararg search: SearchResultEntity)

    @Query("SELECT * FROM search_results")
    fun get(): PagingSource<Int, SearchResultEntity>

    @Query("SELECT * FROM search_results WHERE id IN (:searchIds)")
    fun get(searchIds: List<String>): PagingSource<Int, SearchResultEntity>

    @RawQuery(observedEntities = [SearchResultEntity::class])
    fun get(query: SupportSQLiteQuery): PagingSource<Int, SearchResultEntity>

    @Query("SELECT * FROM search_results WHERE id LIKE :id LIMIT 1")
    suspend fun find(id: String): SearchResultEntity?

    @Query("SELECT EXISTS (SELECT 1 FROM search_results WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM search_results) == 0")
    suspend fun isEmpty(): Boolean

    @Delete
    suspend fun delete(search: SearchResultEntity): Int

    @Query("DELETE FROM search_results WHERE id = :id")
    suspend fun delete(id: String): Int

    @RawQuery(observedEntities = [SearchResultEntity::class])
    suspend fun delete(query: SupportSQLiteQuery): Int

    @Query("DELETE FROM search_results")
    suspend fun delete(): Int
}