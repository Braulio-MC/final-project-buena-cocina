package com.bmc.buenacocina.ui.navigation.graph

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.bmc.buenacocina.ui.navigation.Graph
import com.bmc.buenacocina.ui.navigation.Screen
import com.bmc.buenacocina.ui.screen.category.restaurant.StoreScreen
import com.bmc.buenacocina.ui.screen.chat.ChatScreen
import com.bmc.buenacocina.ui.screen.chat.DetailedChatScreen
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderScreen
import com.bmc.buenacocina.ui.screen.detailed.order.rating.DetailedOrderRatingScreen
import com.bmc.buenacocina.ui.screen.detailed.product.DetailedProductScreen
import com.bmc.buenacocina.ui.screen.detailed.store.DetailedStoreScreen
import com.bmc.buenacocina.ui.screen.home.HomeScreen
import com.bmc.buenacocina.ui.screen.orderhistory.OrderHistoryScreen
import com.bmc.buenacocina.ui.screen.search.SearchScreen
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartScreen
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory
import io.getstream.chat.android.models.Channel

fun NavGraphBuilder.mainGraph(
    windowSizeClass: WindowSizeClass,
    channelViewModelFactory: ChannelViewModelFactory,
    onSearchBarButton: () -> Unit,
    onStoreCategoryBackButton: () -> Unit,
    onStoreCategoryButton: () -> Unit,
    onStoreCategoryStore: (String) -> Unit,
    onDetailedStoreBackButton: () -> Unit,
    onDetailedStoreProductClick: (String, String) -> Unit,
    onDetailedProductBackButton: () -> Unit,
    onProductAddedToCartSuccessful: () -> Unit,
    onOrderHistoryBackButton: () -> Unit,
    onOrderHistoryItemClick: (String) -> Unit,
    onDetailedOrderBackButton: () -> Unit,
    onDetailedOrderChannelCreatedSuccessful: (String) -> Unit,
    onDetailedOrderOrderRating: (String) -> Unit,
    onDetailedOrderOrderRatingUpdatedSuccessful: () -> Unit,
    onDetailedOrderRatingBackButton: () -> Unit,
    onShoppingCartBackButton: () -> Unit,
    onShoppingCartExploreStoresButton: () -> Unit,
    onShoppingCartSuccessfulOrderCreated: () -> Unit,
    onChatBackButton: () -> Unit,
    onChatItemClick: (Channel) -> Unit,
    onDetailedChatBackButton: () -> Unit,
    onLogoutButton: (Boolean) -> Unit
) {
    navigation(
        startDestination = Screen.Main.Home.route,
        route = Graph.Main.MainGraph.route
    ) {
        homeScreen(
            windowSizeClass = windowSizeClass,
            onSearchBarButton = onSearchBarButton,
            onStoreCategoryButton = onStoreCategoryButton,
            onLogoutButton = onLogoutButton
        )
        searchScreen()
        storeCategoryScreen(
            windowSizeClass = windowSizeClass,
            onSearchBarButton = onSearchBarButton,
            onStore = onStoreCategoryStore,
            onBackButton = onStoreCategoryBackButton
        )
        detailedStoreScreen(
            windowSizeClass = windowSizeClass,
            onProductClick = onDetailedStoreProductClick,
            onBackButton = onDetailedStoreBackButton
        )
        detailedProductScreen(
            windowSizeClass = windowSizeClass,
            onProductAddedToCartSuccessful = onProductAddedToCartSuccessful,
            onBackButton = onDetailedProductBackButton
        )
        orderHistoryScreen(
            windowSizeClass = windowSizeClass,
            onOrderHistoryItemClick = onOrderHistoryItemClick,
            onBackButton = onOrderHistoryBackButton
        )
        detailedOrderScreen(
            windowSizeClass = windowSizeClass,
            onChannelCreatedSuccessful = onDetailedOrderChannelCreatedSuccessful,
            onOrderRating = onDetailedOrderOrderRating,
            onBackButton = onDetailedOrderBackButton
        )
        detailedOrderRatingScreen(
            windowSizeClass = windowSizeClass,
            onOrderRatingUpdatedSuccessful = onDetailedOrderOrderRatingUpdatedSuccessful,
            onBackButton = onDetailedOrderRatingBackButton
        )
        shoppingCartScreen(
            windowSizeClass = windowSizeClass,
            onBackButton = onShoppingCartBackButton,
            onExploreStoresButton = onShoppingCartExploreStoresButton,
            onShoppingCartSuccessfulOrderCreated = onShoppingCartSuccessfulOrderCreated
        )
        chatScreen(
            viewModel = channelViewModelFactory,
            onItemClick = onChatItemClick,
            onBackButton = onChatBackButton
        )
        detailedChatScreen(
            onBackButton = onDetailedChatBackButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeScreen(
    windowSizeClass: WindowSizeClass,
    onSearchBarButton: () -> Unit,
    onStoreCategoryButton: () -> Unit,
    onLogoutButton: (Boolean) -> Unit
) {
    composable(Screen.Main.Home.route) {
        HomeScreen(
            windowSizeClass = windowSizeClass,
            onSearchBarButton = onSearchBarButton,
            onStoreCategoryButton = onStoreCategoryButton,
            onLogoutButton = onLogoutButton
        )
    }
}

fun NavGraphBuilder.searchScreen() {
    composable(Screen.Main.Search.route) {
        SearchScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.storeCategoryScreen(
    windowSizeClass: WindowSizeClass,
    onSearchBarButton: () -> Unit,
    onStore: (String) -> Unit,
    onBackButton: () -> Unit
) {
    composable(Screen.Main.StoreCategory.route) {
        StoreScreen(
            windowSizeClass = windowSizeClass,
            onSearchBarButton = onSearchBarButton,
            onStore = onStore,
            onBackButton = onBackButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.detailedStoreScreen(
    windowSizeClass: WindowSizeClass,
    onProductClick: (String, String) -> Unit,
    onBackButton: () -> Unit
) {
    composable<Screen.MainSerializable.StoreDetailed> {
        val nav = it.toRoute<Screen.MainSerializable.StoreDetailed>()
        DetailedStoreScreen(
            windowSizeClass = windowSizeClass,
            storeId = nav.storeId,
            onProductClick = onProductClick,
            onBackButton = onBackButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.detailedProductScreen(
    windowSizeClass: WindowSizeClass,
    onProductAddedToCartSuccessful: () -> Unit,
    onBackButton: () -> Unit
) {
    composable<Screen.MainSerializable.ProductDetailed> {
        val nav = it.toRoute<Screen.MainSerializable.ProductDetailed>()
        DetailedProductScreen(
            windowSizeClass = windowSizeClass,
            productId = nav.productId,
            storeOwnerId = nav.storeOwnerId,
            onProductAddedToCartSuccessful = onProductAddedToCartSuccessful,
            onBackButton = onBackButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.orderHistoryScreen(
    windowSizeClass: WindowSizeClass,
    onOrderHistoryItemClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    composable(Screen.Main.OrderHistory.route) {
        OrderHistoryScreen(
            windowSizeClass = windowSizeClass,
            onOrderItemClick = onOrderHistoryItemClick,
            onBackButton = onBackButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.detailedOrderScreen(
    windowSizeClass: WindowSizeClass,
    onChannelCreatedSuccessful: (String) -> Unit,
    onOrderRating: (String) -> Unit,
    onBackButton: () -> Unit
) {
    composable<Screen.MainSerializable.OrderDetailed> {
        val nav = it.toRoute<Screen.MainSerializable.OrderDetailed>()
        DetailedOrderScreen(
            windowSizeClass = windowSizeClass,
            orderId = nav.orderId,
            onChannelCreatedSuccessful = onChannelCreatedSuccessful,
            onOrderRating = onOrderRating,
            onBackButton = onBackButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.detailedOrderRatingScreen(
    windowSizeClass: WindowSizeClass,
    onOrderRatingUpdatedSuccessful: () -> Unit,
    onBackButton: () -> Unit
) {
    composable<Screen.MainSerializable.OrderRating> {
        val nav = it.toRoute<Screen.MainSerializable.OrderRating>()
        DetailedOrderRatingScreen(
            windowSizeClass = windowSizeClass,
            orderId = nav.orderId,
            onOrderRatingUpdatedSuccessful = onOrderRatingUpdatedSuccessful,
            onBackButton = onBackButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.shoppingCartScreen(
    windowSizeClass: WindowSizeClass,
    onBackButton: () -> Unit,
    onExploreStoresButton: () -> Unit,
    onShoppingCartSuccessfulOrderCreated: () -> Unit
) {
    composable(Screen.Main.ShoppingCart.route) {
        ShoppingCartScreen(
            windowSizeClass = windowSizeClass,
            onBackButton = onBackButton,
            onExploreStoresButton = onExploreStoresButton,
            onSuccessfulOrderCreated = onShoppingCartSuccessfulOrderCreated
        )
    }
}

fun NavGraphBuilder.chatScreen(
    viewModel: ChannelViewModelFactory,
    onItemClick: (Channel) -> Unit,
    onBackButton: () -> Unit
) {
    composable(Screen.Main.Chat.route) {
        ChatScreen(
            viewModel = viewModel,
            onItemClick = onItemClick,
            onBackButton = onBackButton
        )
    }
}

fun NavGraphBuilder.detailedChatScreen(
    onBackButton: () -> Unit
) {
    composable<Screen.MainSerializable.ChatDetailed> {
        val nav = it.toRoute<Screen.MainSerializable.ChatDetailed>()
        DetailedChatScreen(
            channelId = nav.channelId,
            onBackButton = onBackButton
        )
    }
}
