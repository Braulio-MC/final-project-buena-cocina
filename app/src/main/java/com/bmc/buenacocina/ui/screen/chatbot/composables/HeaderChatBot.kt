package com.bmc.buenacocina.ui.screen.chatbot.composables



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderChatBot() {
    // Barra superior con título y botón de retroceso
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                // Añadir un poco de espacio lateral
                verticalAlignment = Alignment.CenterVertically, // Alinea el contenido verticalmente
                horizontalArrangement = Arrangement.Center // Alinea los elementos al inicio
            ) {

                // Título centrado
                Text(
                    text = "ChatBot MiTienda CUCEI",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp), // Tamaño de fuente personalizado
                    modifier = Modifier.weight(1f).padding(30.dp),// Hace que el texto ocupe el espacio restante
                    textAlign = TextAlign.Center // Alinea el texto al centro
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp), // Altura ajustada del header
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF435C99) // Color personalizado
        )
    )
}






