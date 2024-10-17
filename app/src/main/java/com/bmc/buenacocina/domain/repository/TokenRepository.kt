package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.service.TokenService
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val tokenService: TokenService
) {
    suspend fun create(
        token: String,
        onSuccess: (Any?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        tokenService.create(token, onSuccess, onFailure)
    }

    fun exists(
        userId: String?,
        token: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        tokenService.exists(userId, token, onSuccess, onFailure)
    }

    suspend fun remove(
        token: String? = null,
        onSuccess: (Any?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        tokenService.remove(token, onSuccess, onFailure)
    }
}