package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.model.GetStreamUserCredentials
import com.bmc.buenacocina.data.preferences.PreferencesService
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.InitializationState
import io.getstream.result.Error
import kotlinx.coroutines.flow.transformWhile
import javax.inject.Inject

class ChatService @Inject constructor(
    private val chatClient: ChatClient,
    private val preferencesService: PreferencesService
) {
    suspend fun connectUser(
        credentials: GetStreamUserCredentials,
        onSuccess: () -> Unit,
        onFailure: (Error) -> Unit
    ) {
        chatClient.run {
            clientState.initializationState
                .transformWhile {
                    emit(it)
                    it != InitializationState.COMPLETE
                }
                .collect {
                    if (it == InitializationState.NOT_INITIALIZED) {
                        connectUser(credentials.user, credentials.token)
                            .enqueue { result ->
                                result.onError(onFailure)
                                    .onSuccess {
                                        preferencesService.saveUserCredentials(credentials)
                                        onSuccess()
                                    }
                            }
                    }
                }
        }
    }

    suspend fun disconnectUser() {
        preferencesService.clearUserCredentials()
        chatClient.disconnect(false).await()
    }
}