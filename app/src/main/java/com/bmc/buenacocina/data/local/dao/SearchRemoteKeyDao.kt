package com.bmc.buenacocina.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bmc.buenacocina.data.local.model.SearchRemoteKeyEntity

@Dao
interface SearchRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(searchRemoteKeyEntity: SearchRemoteKeyEntity): Long

    @Query("SELECT * FROM search_remote_keys WHERE label = :label")
    suspend fun find(label: String): SearchRemoteKeyEntity

    @Query("DELETE FROM search_remote_keys WHERE label = :label")
    suspend fun delete(label: String): Int

    @Query("DELETE FROM search_remote_keys")
    suspend fun delete(): Int
}