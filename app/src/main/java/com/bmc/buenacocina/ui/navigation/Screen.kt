package com.bmc.buenacocina.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    sealed class Main(val route: String) : Screen {
        data object Home : Main("home")
        data object StoreCategory : Main("storeCategory")
        data object OrderHistory : Main("orderHistory")
        data object Search : Main("search")
        data object ShoppingCart : Main("shoppingCart")
        data object Chat : Main("chat")
    }

    sealed class MainSerializable : Screen {
        @Serializable
        data class StoreDetailed(val storeId: String) : MainSerializable()
        @Serializable
        data class ProductDetailed(val productId: String) : MainSerializable()
        @Serializable
        data class OrderDetailed(val orderId: String) : MainSerializable()
        @Serializable
        data class ChatDetailed(val channelId: String) : MainSerializable()
    }

    sealed class Auth(val route: String) : Screen {
        data object Login : Auth("login")
    }
}