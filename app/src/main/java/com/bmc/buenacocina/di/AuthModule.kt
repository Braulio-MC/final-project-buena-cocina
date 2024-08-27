package com.bmc.buenacocina.di

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.bmc.buenacocina.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Singleton
    @Provides
    fun provideAuth0Account(@ApplicationContext appContext: Context): Auth0 {
        return Auth0(
            appContext.getString(R.string.com_auth0_client_id),
            appContext.getString(R.string.com_auth0_domain)
        )
    }

    @Singleton
    @Provides
    fun provideAuth0ApiClient(auth0: Auth0): AuthenticationAPIClient {
        return AuthenticationAPIClient(auth0)
    }

    @Singleton
    @Provides
    fun provideAuth0SecureCredentialsManager(
        @ApplicationContext appContext: Context,
        authenticationAPIClient: AuthenticationAPIClient
    ): SecureCredentialsManager {
        return SecureCredentialsManager(
            appContext,
            authenticationAPIClient,
            SharedPreferencesStorage(appContext)
        )
    }
}