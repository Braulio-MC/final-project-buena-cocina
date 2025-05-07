package com.bmc.buenacocina.data.network.service

import android.util.Log
import com.bmc.buenacocina.di.AppDispatcher
import com.bmc.buenacocina.di.AppDispatchers
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService : FirebaseMessagingService() {
    @Inject
    lateinit var tokenService: TokenService

    @Inject
    @AppDispatcher(AppDispatchers.IO)
    lateinit var ioDispatcher: CoroutineDispatcher

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(ioDispatcher).launch {
            tokenService.create(
                token = token,
                onSuccess = { msg ->
                    Log.d("PushNotificationService", msg)
                },
                onFailure = { msg, details ->
                    Log.d("PushNotificationService", "$msg: $details")
                }
            )
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}