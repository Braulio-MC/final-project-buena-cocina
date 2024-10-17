package com.bmc.buenacocina.data.network.service

import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.result.UserProfile
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.AuthError
import javax.inject.Inject

class UserService @Inject constructor(
    private val auth0Manager: SecureCredentialsManager
) {
    suspend fun getUserProfile(): Result<UserProfile, AuthError> {
        return if (auth0Manager.hasValidCredentials()) {
            val userProfile = auth0Manager.awaitCredentials().user
            Result.Success(userProfile)
        } else {
            Result.Error(AuthError.NOT_AUTHENTICATED)
        }
    }

    suspend fun getUserId(): Result<String, AuthError> {
        return if (auth0Manager.hasValidCredentials()) {
            return when (val userId = auth0Manager.awaitCredentials().user.getId()) {
                null -> Result.Error(AuthError.NOT_VALID_USER_ID)
                else -> Result.Success(userId)
            }
        } else {
            Result.Error(AuthError.NOT_AUTHENTICATED)
        }
    }
}