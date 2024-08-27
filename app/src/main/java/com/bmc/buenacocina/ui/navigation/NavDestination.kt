package com.bmc.buenacocina.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.bmc.buenacocina.R

enum class NavDestination(
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val contentDescription: Int,
    val route: String
) {
    HOME(
        label = R.string.navigation_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        contentDescription = R.string.navigation_home_content_desc,
        route = Screen.Main.Home.route
    ),
    ORDER_HISTORY(
        label = R.string.navigation_order_history,
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History,
        contentDescription = R.string.navigation_order_history_content_desc,
        route = Screen.Main.OrderHistory.route
    ),
    SHOPPING_CART(
        label = R.string.navigation_shopping_cart,
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        contentDescription = R.string.navigation_shopping_cart_content_desc,
        route = Screen.Main.ShoppingCart.route
    ),
    CHAT(
        label = R.string.navigation_chat,
        selectedIcon = Icons.Filled.ChatBubble,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        contentDescription = R.string.navigation_chat_content_desc,
        route = Screen.Main.Chat.route
    )
}