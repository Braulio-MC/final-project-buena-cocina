package com.bmc.buenacocina.ui.screen.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.ui.common.state.messages.list.DeletedMessageVisibility

@Composable
fun DetailedChatScreen(
    channelId: String,
    onBackButton: () -> Unit,
) {
    val currentContext = LocalContext.current
    val factory by lazy {
        MessagesViewModelFactory(
            context = currentContext,
            channelId = channelId,
            autoTranslationEnabled = true,
            deletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,
        )
    }

    ChatTheme {
        MessagesScreen(
            viewModelFactory = factory,
            onBackPressed = onBackButton
        )
    }
}