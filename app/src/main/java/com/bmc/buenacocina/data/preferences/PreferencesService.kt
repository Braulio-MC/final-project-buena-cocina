package com.bmc.buenacocina.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bmc.buenacocina.data.network.model.GetStreamUserCredentials
import com.bmc.buenacocina.di.AppDispatcher
import com.bmc.buenacocina.di.AppDispatchers
import io.getstream.chat.android.models.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class PreferencesService @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @AppDispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : IPreferences {
    override suspend fun set(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    fun saveUserCredentials(credentials: GetStreamUserCredentials) {
        with(credentials) {
            CoroutineScope(ioDispatcher).launch {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(KEY_API_KEY)] = apiKey
                    preferences[stringPreferencesKey(KEY_USER_ID)] = user.id
                    preferences[stringPreferencesKey(KEY_USER_NAME)] = user.name
                    preferences[stringPreferencesKey(KEY_USER_IMAGE)] = user.image
                    preferences[stringPreferencesKey(KEY_USER_TOKEN)] = token
                }
            }
        }
    }

    suspend fun getUserCredentials(): GetStreamUserCredentials? {
        val apiKey = dataStore.data.first()[stringPreferencesKey(KEY_API_KEY)] ?: return null
        val userId = dataStore.data.first()[stringPreferencesKey(KEY_USER_ID)] ?: return null
        val userName = dataStore.data.first()[stringPreferencesKey(KEY_USER_NAME)] ?: return null
        val userImage = dataStore.data.first()[stringPreferencesKey(KEY_USER_IMAGE)] ?: return null
        val token = dataStore.data.first()[stringPreferencesKey(KEY_USER_TOKEN)] ?: return null

        return GetStreamUserCredentials(
            apiKey = apiKey,
            user = User(
                id = userId,
                name = userName,
                image = userImage
            ),
            token = token
        )
    }

    suspend fun clearUserCredentials() {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(KEY_API_KEY))
            preferences.remove(stringPreferencesKey(KEY_USER_ID))
            preferences.remove(stringPreferencesKey(KEY_USER_NAME))
            preferences.remove(stringPreferencesKey(KEY_USER_IMAGE))
            preferences.remove(stringPreferencesKey(KEY_USER_TOKEN))
        }
    }

    override suspend fun get(key: String): String? {
        val preferencesKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun remove(key: String) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences.remove(preferencesKey)
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private const val KEY_API_KEY = "getstream_api_key"
        private const val KEY_USER_ID = "getstream_user_id"
        private const val KEY_USER_NAME = "getstream_user_name"
        private const val KEY_USER_IMAGE = "getstream_user_image"
        private const val KEY_USER_TOKEN = "getstream_user_token"
    }
}