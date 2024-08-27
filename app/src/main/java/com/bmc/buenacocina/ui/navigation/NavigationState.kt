package com.bmc.buenacocina.ui.navigation

sealed class NavigationState {
    data object Loading : NavigationState()
    data object NotAuthenticated : NavigationState()
    data object Authenticated : NavigationState()
}