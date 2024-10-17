package com.bmc.buenacocina.domain.repository

import com.auth0.android.result.UserProfile
import com.bmc.buenacocina.data.network.service.UserService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.AuthError
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {
    suspend fun getUserProfile(): Result<UserProfile, AuthError> {
        return userService.getUserProfile()
    }

    suspend fun getUserId(): Result<String, AuthError> {
        return userService.getUserId()
    }
}
