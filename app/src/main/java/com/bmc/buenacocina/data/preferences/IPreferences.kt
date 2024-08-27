package com.bmc.buenacocina.data.preferences

interface IPreferences {
    suspend fun set(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
}