package com.bmc.buenacocina.ui.screen.chatbot.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.data.network.model.ChatBotMessageNetwork
import com.bmc.buenacocina.R

@Composable
fun ChatBubble(message: ChatBotMessageNetwork) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) { // Si el mensaje es del bot
            // Usar el Vector Drawable del bot (SVG)
            Icon(
                painter = painterResource(id = R.drawable.chatbot_icon), // Reemplaza con el nombre de tu archivo SVG
                contentDescription = "Bot Icon",
                modifier = Modifier
                    .size(60.dp) // Tamaño del ícono
                    .padding(end = 8.dp), // Separación entre el ícono y el mensaje
                tint = Color.Black,
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = if (message.isUser) Color(0xFF625b71) else Color(0xFFD0BCFF),
                    shape = RoundedCornerShape(
                        topStart = if (message.isUser) 16.dp else 0.dp,
                        topEnd = if (message.isUser) 0.dp else 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
