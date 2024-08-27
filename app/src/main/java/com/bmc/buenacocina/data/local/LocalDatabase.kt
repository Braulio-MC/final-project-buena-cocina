package com.bmc.buenacocina.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bmc.buenacocina.data.local.dao.SearchDao
import com.bmc.buenacocina.data.local.dao.SearchRemoteKeyDao
import com.bmc.buenacocina.data.local.model.SearchRemoteKeyEntity
import com.bmc.buenacocina.data.local.model.SearchResultEntity

@Database(
    entities = [
        SearchRemoteKeyEntity::class,
        SearchResultEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getSearchRemoteKeyDao(): SearchRemoteKeyDao
    abstract fun getSearchDao(): SearchDao
}