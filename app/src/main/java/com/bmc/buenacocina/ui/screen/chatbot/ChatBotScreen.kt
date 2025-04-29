package com.bmc.buenacocina.ui.screen.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.ui.viewmodel.ChatBotViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bmc.buenacocina.ui.screen.chatbot.composables.ChatBubble
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.Alignment
import com.bmc.buenacocina.ui.screen.chatbot.composables.HeaderChatBot
import kotlin.collections.isNotEmpty

@Composable
fun ChatBotScreen(viewModel: ChatBotViewModel = viewModel(), modifier: Modifier = Modifier) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) { //  Evita hacer scroll si la lista está vacía
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderChatBot()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.size) { index ->
                ChatBubble(messages[messages.size - 1 - index]) // Invierte los mensajes para que se vean bien
            }
        }

        // Mostrar el spinner de carga si está cargando
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator() // El spinner
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                placeholder = { Text("Escribe tu mensaje...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF6650a4),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color(0xFF6650a4)
                ),
            )

            Button(
                onClick = {
                    if (userInput.text.isNotBlank()) {
                        viewModel.getChatbotResponse(userInput.text)
                        userInput = TextFieldValue("") // Limpia el campo después de enviar
                    }
                },
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (userInput.text.isNotBlank()) Color(0xFF6650a4) else Color(0xFFCCC2DC)
                ), // Condicional para cambiar el color
                contentPadding = PaddingValues(0.dp) // Elimina el padding interno
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Enviar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }


        }
    }
}
