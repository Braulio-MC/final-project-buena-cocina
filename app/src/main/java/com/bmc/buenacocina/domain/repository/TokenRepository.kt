package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.service.TokenService
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val tokenService: TokenService
) {
    suspend fun create(
        token: String? = null,
        onSuccess: (Any?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        tokenService.create(token, onSuccess, onFailure)
    }

    suspend fun remove(
        token: String? = null,
        onSuccess: (Any?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        tokenService.remove(token, onSuccess, onFailure)
    }
}