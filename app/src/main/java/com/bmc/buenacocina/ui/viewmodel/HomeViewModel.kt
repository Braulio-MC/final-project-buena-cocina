package com.bmc.buenacocina.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.data.preferences.PreferencesService
import com.bmc.buenacocina.domain.repository.UserRepository
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.repository.ChatRepository
import com.bmc.buenacocina.domain.repository.InsightRepository
import com.bmc.buenacocina.domain.repository.TokenRepository
import com.bmc.buenacocina.ui.screen.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth0Account: Auth0,
    private val auth0Manager: SecureCredentialsManager,
    private val userRepository: UserRepository,
    private val preferencesService: PreferencesService,
    private val chatRepository: ChatRepository,
    private val tokenRepository: TokenRepository,
    private val insightRepository: InsightRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState
        .onStart {
            getUserProfile()
            getTopSoldProducts()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = HomeUiState()
        )

    private fun getUserProfile() {
        viewModelScope.launch {
            when (val result = userRepository.getUserProfile()) {
                is Result.Error -> {

                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(userProfile = result.data)
                    }
                }
            }
        }
    }

    fun startLogout(
        c: Context,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        WebAuthProvider
            .logout(auth0Account)
            .withScheme(c.getString(R.string.com_auth0_scheme))
            .start(c, object :
                Callback<Void?, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    onError()
                }

                override fun onSuccess(result: Void?) {
                    viewModelScope.launch {
                        tokenRepository.remove(
                            onSuccess = { },
                            onFailure = { }
                        )
                        auth0Manager.clearCredentials()
                        chatRepository.disconnectUser()
                        preferencesService.clearUserCredentials()
                        onSuccess()
                    }
                }
            })
    }

    private fun getTopSoldProducts() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoadingTopSoldProducts = true)
            }
            when (val response = insightRepository.getTopSoldProducts()) {
                is Result.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingTopSoldProducts = false)
                    }
                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoadingTopSoldProducts = false,
                            topSoldProducts = response.data
                        )
                    }
                }
            }
        }
    }
}