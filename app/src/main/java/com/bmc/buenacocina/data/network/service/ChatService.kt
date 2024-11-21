package com.bmc.buenacocina.data.network.service

import android.content.Context
import android.util.Log
import com.bmc.buenacocina.R
import com.bmc.buenacocina.data.network.model.GetStreamUserCredentials
import com.bmc.buenacocina.data.preferences.PreferencesService
import com.bmc.buenacocina.domain.repository.GetStreamTokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import com.bmc.buenacocina.domain.Result as ResultDomain
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ChatService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatClient: ChatClient,
    private val preferencesService: PreferencesService,
    private val userService: UserService,
    private val getStreamTokenRepository: GetStreamTokenRepository
) {
    suspend fun connectUser(credentials: GetStreamUserCredentials?): Result<Unit> = runCatching {
        if (credentials != null) {
            connectUserInternal(credentials)
        } else {
            when (val userResult = userService.getUserProfile()) {
                is ResultDomain.Error -> {
                    Log.e("ChatService", "Error on getUserProfile: ${userResult.error}")
                }

                is ResultDomain.Success -> {
                    val userId = userResult.data.getId()
                    val userName = userResult.data.name ?: userResult.data.nickname
                    val image = userResult.data.pictureURL

                    if (userId != null && userName != null && image != null) {
                        val id = userId.replace("|", "-")
                        when (val tokenResult = getStreamTokenRepository.request(id)) {
                            is ResultDomain.Error -> {
                                Log.e(
                                    "ChatService",
                                    "Error on request token: ${tokenResult.error}"
                                )
                            }

                            is ResultDomain.Success -> {
                                val getStreamCredentials = GetStreamUserCredentials(
                                    apiKey = context.getString(R.string.get_stream_api_key),
                                    user = User(
                                        id = id,
                                        name = userName,
                                        image = image
                                    ),
                                    token = tokenResult.data
                                )
                                connectUserInternal(getStreamCredentials)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun connectUserInternal(credentials: GetStreamUserCredentials) {
        val initializationState = awaitInitializationState()
        if (initializationState == InitializationState.COMPLETE) return

        suspendCancellableCoroutine { continuation ->
            chatClient.connectUser(credentials.user, credentials.token)
                .enqueue { response ->
                    if (response.isSuccess) {
                        preferencesService.saveUserCredentials(credentials)
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(Exception(response.errorOrNull()?.message)) // TODO: Custom error here
                    }
                }
            continuation.invokeOnCancellation {
                try {
                    chatClient.disconnect(false).enqueue()
                    preferencesService.clearUserCredentials()
                } catch (e: Exception) {
                    Log.e("ChatService", "Error on disconnect: $e")
                }
            }
        }
    }

    private suspend fun awaitInitializationState(): InitializationState =
        chatClient.clientState.initializationState.first { state ->
            state == InitializationState.NOT_INITIALIZED || state == InitializationState.COMPLETE
        }


    suspend fun disconnectUser() {
        chatClient.disconnect(false).await()
        preferencesService.clearUserCredentials()
    }
}