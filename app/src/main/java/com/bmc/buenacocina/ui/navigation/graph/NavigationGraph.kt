package com.bmc.buenacocina.ui.navigation.graph

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bmc.buenacocina.ui.navigation.Graph
import com.bmc.buenacocina.ui.navigation.NavigationState
import com.bmc.buenacocina.ui.screen.MainScreen
import com.bmc.buenacocina.ui.screen.common.NavigationStateLoading
import com.bmc.buenacocina.ui.viewmodel.NavigationViewModel
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory

@Composable
fun NavigationGraph(
    windowSizeClass: WindowSizeClass,
    channelViewModelFactory: ChannelViewModelFactory,
    navController: NavHostController = rememberNavController(),
    viewModel: NavigationViewModel = hiltViewModel(),
    onUserAuthenticated: (String?) -> Unit
) {
    val result by produceState<NavigationState>(initialValue = NavigationState.Loading) {
        value = viewModel.checkNavigationState()
    }

    if (result is NavigationState.Loading) {
        NavigationStateLoading()
        return
    }

    val startDestination = if (result is NavigationState.NotAuthenticated) {
        Graph.Auth.AuthGraph.route
    } else {
        Graph.Main.MainGraph.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            windowSizeClass = windowSizeClass,
            onLoginButton = { isSuccessful, userId ->
                if (isSuccessful) {
                    onUserAuthenticated(userId)
                    navController.navigate(Graph.Main.MainGraph.route) {
                        popUpTo(Graph.Auth.AuthGraph.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
        )
        composable(Graph.Main.MainGraph.route) {
            MainScreen(
                windowSizeClass = windowSizeClass,
                channelViewModelFactory = channelViewModelFactory,
                onLogoutButton = { isSuccessful ->
                    if (isSuccessful) {
                        navController.navigate(Graph.Auth.AuthGraph.route) {
                            popUpTo(Graph.Main.MainGraph.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}