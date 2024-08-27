package com.bmc.buenacocina.ui.navigation

sealed interface Graph {
    sealed class Auth(val route: String) : Graph {
        data object AuthGraph : Auth("auth_graph")
    }

    sealed class Main(val route: String) : Graph {
        data object MainGraph : Main("main_graph")
    }
}