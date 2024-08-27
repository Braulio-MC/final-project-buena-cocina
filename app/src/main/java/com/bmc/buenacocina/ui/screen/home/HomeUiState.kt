package com.bmc.buenacocina.ui.screen.home

import com.auth0.android.result.UserProfile

data class HomeUiState(
    val userProfile: UserProfile? = null
)