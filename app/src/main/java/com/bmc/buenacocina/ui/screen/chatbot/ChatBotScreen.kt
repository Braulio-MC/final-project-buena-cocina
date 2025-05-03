package com.bmc.buenacocina.ui.screen.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.ui.viewmodel.ChatBotViewModel
import com.bmc.buenacocina.ui.screen.chatbot.composables.ChatBubble
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bmc.buenacocina.domain.isScrolledToEnd
import com.bmc.buenacocina.ui.screen.chatbot.composables.HeaderChatBot
import com.bmc.buenacocina.ui.screen.chatbot.composables.KeyboardState
import com.bmc.buenacocina.ui.screen.chatbot.composables.ScrollToBottomButton
import com.bmc.buenacocina.ui.screen.chatbot.composables.TypingIndicator
import com.bmc.buenacocina.ui.screen.chatbot.composables.keyboardAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.isNotEmpty

@Composable
fun ChatBotScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatBotViewModel = hiltViewModel(),
    onHeaderBackButton: () -> Unit
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val scrollCoroutineScope = rememberCoroutineScope()
    val isKeyboardOpen by keyboardAsState()

    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen == KeyboardState.Opened && !listState.isScrolledToEnd()) {
            delay(200)
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        HeaderChatBot(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp),
            onBackButton = onHeaderBackButton
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
                if (isLoading) {
                    item {
                        TypingIndicator(
                            modifier = Modifier
                                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                .align(Alignment.CenterStart)
                        )
                    }
                }
            }

            if (messages.size > 5 && !listState.isScrolledToEnd()) {
                ScrollToBottomButton(
                    onClick = {
                        scrollCoroutineScope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp),
                placeholder = {
                    Text(
                        text = "Escribe tu mensaje...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (userInput.text.isNotBlank()) {
                            viewModel.getChatbotResponse(userInput.text)
                            userInput = TextFieldValue("")
                        }
                    }
                ),
                maxLines = 5,
                singleLine = false,
                trailingIcon = {
                    if (userInput.text.isNotBlank()) {
                        IconButton(
                            onClick = { userInput = TextFieldValue("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clean text field",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                }
            )

            IconButton(
                onClick = {
                    if (userInput.text.isNotBlank()) {
                        viewModel.getChatbotResponse(userInput.text)
                        userInput = TextFieldValue("")
                    }
                },
                modifier = Modifier.size(48.dp),
                enabled = userInput.text.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send query",
                    tint = if (userInput.text.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }
        }
    }
}
