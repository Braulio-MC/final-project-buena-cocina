package com.bmc.buenacocina.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.bmc.buenacocina.ui.navigation.NavDestination
import com.bmc.buenacocina.ui.navigation.graph.MainGraph
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory

@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    channelViewModelFactory: ChannelViewModelFactory,
    navController: NavHostController = rememberNavController(),
    onLogoutButton: (Boolean) -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route

    MainScreenContent(
        wsc = windowSizeClass,
        channelViewModelFactory = channelViewModelFactory,
        adaptiveInfo = adaptiveInfo,
        navController = navController,
        currentRoute = currentRoute,
        currentBackStackEntry = currentNavBackStackEntry,
        onLogoutButton = onLogoutButton
    )
}

@Composable
fun MainScreenContent(
    wsc: WindowSizeClass,
    channelViewModelFactory: ChannelViewModelFactory,
    adaptiveInfo: WindowAdaptiveInfo,
    navController: NavHostController,
    currentRoute: String?,
    currentBackStackEntry: NavBackStackEntry?,
    onLogoutButton: (Boolean) -> Unit
) {
    val navSuiteType = with(adaptiveInfo) {
        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }
    val modifier = if (navSuiteType == NavigationSuiteType.NavigationRail) {
        Modifier.padding(5.dp)
    } else {
        Modifier
    }
    val popBackStack = {
        if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.popBackStack()
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            NavDestination.entries.forEach { destination ->
                item(
                    selected = currentRoute == destination.route,
                    onClick = {
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                popUpTo(NavDestination.HOME.route)
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == destination.route) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            },
                            contentDescription = stringResource(id = destination.contentDescription),
                            tint = Color.Black,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = destination.label),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W400,
                            color = Color.Black
                        )
                    },
                    modifier = modifier,
                )
            }
        },
        layoutType = navSuiteType,
    ) {
        MainGraph(
            windowSizeClass = wsc,
            channelViewModelFactory = channelViewModelFactory,
            navController = navController,
            onSearchBackButton = popBackStack,
            onStoreFavoriteBackButton = popBackStack,
            onProductFavoriteBackButton = popBackStack,
            onStoreCategoryBackButton = popBackStack,
            onDetailedStoreBackButton = popBackStack,
            onStoreReviewBackButton = popBackStack,
            onDetailedProductBackButton = popBackStack,
            onProductReviewBackButton = popBackStack,
            onOrderHistoryBackButton = popBackStack,
            onDetailedOrderBackButton = popBackStack,
            onDetailedOrderOrderRatingUpdatedSuccessful = popBackStack,
            onDetailedOrderRatingBackButton = popBackStack,
            onShoppingCartBackButton = popBackStack,
            onChatBackButton = popBackStack,
            onDetailedChatBackButton = popBackStack,
            onLogoutButton = onLogoutButton
        )
    }
}