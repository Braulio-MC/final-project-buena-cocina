package com.bmc.buenacocina.ui.screen.chat

import androidx.compose.runtime.Composable
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory

@Composable
fun DetailedChatScreen(
    viewModelFactory: MessagesViewModelFactory,
    onBackButton: () -> Unit,
) {
    ChatTheme {
        MessagesScreen(
            viewModelFactory = viewModelFactory,
            onBackPressed = onBackButton
        )
    }
}