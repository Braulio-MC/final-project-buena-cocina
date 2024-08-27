package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.bmc.buenacocina.ui.navigation.NavigationState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val auth0Manager: SecureCredentialsManager
) : ViewModel() {
    fun checkNavigationState(): NavigationState {
        if (!auth0Manager.hasValidCredentials()) {
            return NavigationState.NotAuthenticated
        }
        return NavigationState.Authenticated
    }
}