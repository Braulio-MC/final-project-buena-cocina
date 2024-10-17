package com.bmc.buenacocina.data.preferences

import android.content.SharedPreferences
import com.bmc.buenacocina.data.network.model.GetStreamUserCredentials
import io.getstream.chat.android.models.User
import javax.inject.Inject

class PreferencesService @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : IPreferences {
    override fun set(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun saveUserCredentials(credentials: GetStreamUserCredentials) {
        with(credentials) {
            with(sharedPreferences.edit()) {
                putString(KEY_API_KEY, apiKey)
                putString(KEY_USER_ID, user.id)
                putString(KEY_USER_NAME, user.name)
                putString(KEY_USER_IMAGE, user.image)
                putString(KEY_USER_TOKEN, token)
                apply()
            }
        }
    }

    override fun get(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun getUserCredentials(): GetStreamUserCredentials? {
        val apiKey = sharedPreferences.getString(KEY_API_KEY, null) ?: return null
        val userId = sharedPreferences.getString(KEY_USER_ID, null) ?: return null
        val userName = sharedPreferences.getString(KEY_USER_NAME, null) ?: return null
        val userImage = sharedPreferences.getString(KEY_USER_IMAGE, null) ?: return null
        val token = sharedPreferences.getString(KEY_USER_TOKEN, null) ?: return null

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

    override fun remove(key: String) {
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }

    override fun clearUserCredentials() {
        with(sharedPreferences.edit()) {
            remove(KEY_API_KEY)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_IMAGE)
            remove(KEY_USER_TOKEN)
            apply()
        }
    }

    override fun clear() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
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