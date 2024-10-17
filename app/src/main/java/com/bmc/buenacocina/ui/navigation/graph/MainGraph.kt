package com.bmc.buenacocina.ui.navigation.graph

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.bmc.buenacocina.ui.navigation.Graph
import com.bmc.buenacocina.ui.navigation.Screen
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory

@Composable
fun MainGraph(
    windowSizeClass: WindowSizeClass,
    channelViewModelFactory: ChannelViewModelFactory,
    navController: NavHostController,
    onStoreCategoryBackButton: () -> Unit,
    onDetailedStoreBackButton: () -> Unit,
    onDetailedProductBackButton: () -> Unit,
    onOrderHistoryBackButton: () -> Unit,
    onDetailedOrderBackButton: () -> Unit,
    onDetailedOrderOrderRatingUpdatedSuccessful: () -> Unit,
    onDetailedOrderRatingBackButton: () -> Unit,
    onShoppingCartBackButton: () -> Unit,
    onChatBackButton: () -> Unit,
    onDetailedChatBackButton: () -> Unit,
    onLogoutButton: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Graph.Main.MainGraph.route
    ) {
        mainGraph(
            windowSizeClass = windowSizeClass,
            channelViewModelFactory = channelViewModelFactory,
            onSearchBarButton = {
                navController.navigate(Screen.Main.Search.route) {
                    launchSingleTop = true
                }
            },
            onStoreCategoryBackButton = onStoreCategoryBackButton,
            onStoreCategoryButton = {
                navController.navigate(Screen.Main.StoreCategory.route) {
                    launchSingleTop = true
                }
            },
            onStoreCategoryStore = { storeId ->
                navController.navigate(Screen.MainSerializable.StoreDetailed(storeId)) {
                    launchSingleTop = true
                }
            },
            onDetailedStoreBackButton = onDetailedStoreBackButton,
            onDetailedStoreProductClick = { productId, storeOwnerId ->
                navController.navigate(Screen.MainSerializable.ProductDetailed(productId, storeOwnerId)) {
                    launchSingleTop = true
                }
            },
            onDetailedProductBackButton = onDetailedProductBackButton,
            onProductAddedToCartSuccessful = {},
            onOrderHistoryBackButton = onOrderHistoryBackButton,
            onOrderHistoryItemClick = { orderId ->
                navController.navigate(Screen.MainSerializable.OrderDetailed(orderId)) {
                    launchSingleTop = true
                }
            },
            onDetailedOrderBackButton = onDetailedOrderBackButton,
            onDetailedOrderChannelCreatedSuccessful = { channelId ->
                navController.navigate(Screen.MainSerializable.ChatDetailed(channelId)) {
                    popUpTo(Screen.Main.OrderHistory.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            onDetailedOrderOrderRating = { orderId ->
                navController.navigate(Screen.MainSerializable.OrderRating(orderId)) {
                    launchSingleTop = true
                }
            },
            onDetailedOrderOrderRatingUpdatedSuccessful = onDetailedOrderOrderRatingUpdatedSuccessful,
            onDetailedOrderRatingBackButton = onDetailedOrderRatingBackButton,
            onShoppingCartBackButton = onShoppingCartBackButton,
            onShoppingCartExploreStoresButton = {
                navController.navigate(Screen.Main.StoreCategory.route) {
                    popUpTo(Screen.Main.ShoppingCart.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            onShoppingCartSuccessfulOrderCreated = {
                navController.navigate(Screen.Main.OrderHistory.route) {
                    popUpTo(Screen.Main.ShoppingCart.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            onChatBackButton = onChatBackButton,
            onChatItemClick = { channel ->
                navController.navigate(Screen.MainSerializable.ChatDetailed(channel.cid)) {
                    launchSingleTop = true
                }
            },
            onDetailedChatBackButton = onDetailedChatBackButton,
            onLogoutButton = onLogoutButton
        )
    }
}