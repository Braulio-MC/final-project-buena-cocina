package com.bmc.buenacocina.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_remote_keys")
data class SearchRemoteKeyEntity(
    @PrimaryKey
    val label: String,

    @ColumnInfo(name = "nextKey")
    val nextKey: Int?
)
