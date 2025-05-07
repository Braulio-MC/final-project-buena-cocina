package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.service.TokenService
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val tokenService: TokenService
) {
    suspend fun create(
        token: String? = null,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        tokenService.create(token, onSuccess, onFailure)
    }

    suspend fun remove(
        token: String? = null,
        onSuccess: (String, Int) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        tokenService.remove(token, onSuccess, onFailure)
    }
}