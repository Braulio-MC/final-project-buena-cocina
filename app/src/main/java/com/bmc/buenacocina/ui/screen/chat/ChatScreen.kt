package com.bmc.buenacocina.ui.screen.chat

import androidx.compose.runtime.Composable
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory
import io.getstream.chat.android.models.Channel

@Composable
fun ChatScreen(
    viewModel: ChannelViewModelFactory,
    onItemClick: (Channel) -> Unit,
    onBackButton: () -> Unit,
) {
    ChatTheme {
        ChannelsScreen(
            viewModelFactory = viewModel,
            title = "Chats",
            isShowingHeader = true,
            isShowingSearch = true,
            onItemClick = onItemClick,
            onBackPressed = onBackButton
        )
    }
}