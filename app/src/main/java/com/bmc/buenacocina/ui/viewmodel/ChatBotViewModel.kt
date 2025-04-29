package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.data.network.model.ChatBotApiResponse
import com.bmc.buenacocina.domain.repository.ChatbotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.bmc.buenacocina.data.network.model.ChatBotMessageNetwork
import com.bmc.buenacocina.core.getGreetingPerHour

class ChatBotViewModel : ViewModel() {
    private val repository = ChatbotRepository()

    private val _messages = MutableStateFlow<List<ChatBotMessageNetwork>>(emptyList())
    val messages: StateFlow<List<ChatBotMessageNetwork>> get() = _messages

    // Estado para controlar si está cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        // Mensaje inicial del bot cuando se abre el chat
        _messages.value = _messages.value + ChatBotMessageNetwork(getGreetingPerHour(), isUser = false)
    }

    fun getChatbotResponse(question: String) {
        viewModelScope.launch {
            try {
                // Agregar el mensaje del usuario
                _messages.value = _messages.value + ChatBotMessageNetwork(question, isUser = true)

                // Mostrar el spinner de carga
                _isLoading.value = true

                val response = repository.getChatbotData(question)

                // Manejar la respuesta del bot con más detalles
                when (response) {
                    is ChatBotApiResponse.Message -> {
                        // Mensaje de texto, solo devuelve el mensaje con un emoji
                        val botMessage = "${response.message}"
                        _messages.value = _messages.value + ChatBotMessageNetwork(botMessage, isUser = false)
                    }
                    is ChatBotApiResponse.ProductResponse -> {
                        // Enviar cada producto como un mensaje individual
                        for (product in response.data.take(6)) { // Limitar a 6 productos si es necesario
                            val productMessage = """
                            🛍️ Producto: ${product.name}
                            📝 Descripción: ${product.description}
                            💵 Precio: $${product.price}
                            ⭐ Calificación: ${product.rating}
                            🏬 Tienda: ${product.store.name}
                        """.trimIndent()

                            // Agregar el mensaje del producto
                            _messages.value = _messages.value + ChatBotMessageNetwork(productMessage, isUser = false)
                        }
                    }
                    is ChatBotApiResponse.StoreResponse -> {
                        // Enviar cada tienda como un mensaje individual
                        for (store in response.data.take(6)) { // Limitar a 6 tiendas si es necesario
                            val storeMessage = """
                            🏪 Tienda: ${store.name}
                            📝 Descripción: ${store.description}
                            ⭐ Calificación: ${store.rating}
                            ⏰ Horario: ${store.startTime} - ${store.endTime}
                        """.trimIndent()

                            // Agregar el mensaje de la tienda
                            _messages.value = _messages.value + ChatBotMessageNetwork(storeMessage, isUser = false)
                        }
                    }
                }
            } catch (e: Exception) {
                // En caso de error, enviar un mensaje
                _messages.value = _messages.value + ChatBotMessageNetwork("Error al realizar la solicitud", isUser = false)
            } finally {
                // Ocultar el spinner de carga cuando termine
                _isLoading.value = false
            }
        }
    }
}