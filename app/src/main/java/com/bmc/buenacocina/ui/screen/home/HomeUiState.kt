package com.bmc.buenacocina.ui.screen.home

import com.auth0.android.result.UserProfile
import com.bmc.buenacocina.domain.model.InsightTopSoldProductDomain

data class HomeUiState(
    val isLoadingTopSoldProducts: Boolean = false,
    val userProfile: UserProfile? = null,
    val topSoldProducts: List<InsightTopSoldProductDomain> = emptyList(),
)