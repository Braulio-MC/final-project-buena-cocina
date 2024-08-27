package com.bmc.buenacocina.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_results")
data class SearchResultEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "description1")
    val description1: String,

    @ColumnInfo(name = "description2")
    val description2: String
)
