package com.bmc.buenacocina.di

import android.content.Context
import com.bmc.buenacocina.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.chat.android.client.BuildConfig
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GetStreamModule {
    @Provides
    fun provideOfflinePluginFactory(@ApplicationContext context: Context): StreamOfflinePluginFactory {
        return StreamOfflinePluginFactory(context)
    }

    @Provides
    @Singleton
    fun provideStatePluginFactory(@ApplicationContext context: Context): StreamStatePluginFactory {
        return StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true
            ),
            appContext = context
        )
    }

    @Provides
    @Singleton
    fun provideNotificationConfig(): NotificationConfig {
        return NotificationConfig(
            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "FCM"))
        )
    }

    @Provides
    @Singleton
    fun provideNotificationHandler(
        @ApplicationContext context: Context,
        notificationConfig: NotificationConfig
    ): NotificationHandler {
        return NotificationHandlerFactory.createNotificationHandler(
            context = context,
            notificationConfig = notificationConfig
        )
    }

    @Provides
    @Singleton
    fun provideChatClient(
        @ApplicationContext context: Context,
        notificationConfig: NotificationConfig,
        notificationHandler: NotificationHandler,
        offlinePluginFactory: StreamOfflinePluginFactory,
        statePluginFactory: StreamStatePluginFactory
    ): ChatClient {
        val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING
        val apiKey = context.getString(R.string.get_stream_api_key)
        val client = ChatClient.Builder(apiKey, context)
            .notifications(notificationConfig, notificationHandler)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(logLevel)
            .build()
        return client
    }
}