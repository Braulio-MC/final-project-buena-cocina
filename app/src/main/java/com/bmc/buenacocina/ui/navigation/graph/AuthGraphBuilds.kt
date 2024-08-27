package com.bmc.buenacocina.ui.navigation.graph

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.bmc.buenacocina.ui.navigation.Graph
import com.bmc.buenacocina.ui.navigation.Screen
import com.bmc.buenacocina.ui.screen.login.LoginScreen

fun NavGraphBuilder.authGraph(
    windowSizeClass: WindowSizeClass,
    onLoginButton: (Boolean, String?) -> Unit,
) {
    navigation(
        startDestination = Screen.Auth.Login.route,
        route = Graph.Auth.AuthGraph.route
    ) {
        loginScreen(
            windowSizeClass = windowSizeClass,
            onLoginButton = onLoginButton
        )
    }
}

fun NavGraphBuilder.loginScreen(
    windowSizeClass: WindowSizeClass,
    onLoginButton: (Boolean, String?) -> Unit,
) {
    composable(Screen.Auth.Login.route) {
        LoginScreen(
            windowSizeClass = windowSizeClass,
            onLoginButton = onLoginButton
        )
    }
}