package com.bmc.buenacocina.di

import android.content.Context
import com.algolia.client.api.SearchClient
import com.bmc.buenacocina.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlgoliaModule {
    @Provides
    @Singleton
    fun provideAlgoliaClient(@ApplicationContext context: Context): SearchClient {
        val appId = context.getString(R.string.algolia_search_app_id)
        val searchApiKey = context.getString(R.string.algolia_search_search_api_key)
        val client = SearchClient(appId, searchApiKey)
        return client
    }

    @Provides
    fun provideAlgoliaClientFactory(@ApplicationContext context: Context): AlgoliaClientFactory {
        return object : AlgoliaClientFactory {
            override fun create(scopedSecuredApiKey: String): SearchClient {
                val appId = context.getString(R.string.algolia_search_app_id)
                val client = SearchClient(appId, scopedSecuredApiKey)
                return client
            }
        }
    }
}