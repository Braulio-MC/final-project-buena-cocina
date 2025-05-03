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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val repository: ChatbotRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatBotMessageNetwork>>(emptyList())
    val messages: StateFlow<List<ChatBotMessageNetwork>> get() = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        _messages.value += ChatBotMessageNetwork(getGreetingPerHour(), isUser = false)
    }

    fun getChatbotResponse(question: String) {
        viewModelScope.launch {
            try {
                _messages.value += ChatBotMessageNetwork(question, isUser = true)
                _isLoading.value = true
                when (val response = repository.getChatbotData(question)) {
                    is ChatBotApiResponse.Message -> {
                        val botMessage = response.message
                        _messages.value += ChatBotMessageNetwork(botMessage, isUser = false)
                    }
                    is ChatBotApiResponse.ProductResponse -> {
                        for (product in response.data.take(6)) {
                            val productMessage = """
                            🛍️ Producto: ${product.name}
                            📝 Descripción: ${product.description}
                            💵 Precio: $${product.price}
                            ⭐ Calificación: ${product.rating}
                            🏬 Tienda: ${product.store.name}
                        """.trimIndent()
                            _messages.value += ChatBotMessageNetwork(productMessage, isUser = false)
                        }
                    }
                    is ChatBotApiResponse.StoreResponse -> {
                        for (store in response.data.take(6)) {
                            val storeMessage = """
                            🏪 Tienda: ${store.name}
                            📝 Descripción: ${store.description}
                            ⭐ Calificación: ${store.rating}
                            ⏰ Horario: ${store.startTime} - ${store.endTime}
                        """.trimIndent()
                            _messages.value += ChatBotMessageNetwork(storeMessage, isUser = false)
                        }
                    }
                }
            } catch (e: Exception) {
                _messages.value += ChatBotMessageNetwork(
                    "Error al realizar la solicitud",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}