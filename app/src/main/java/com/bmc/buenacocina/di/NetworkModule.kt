package com.bmc.buenacocina.di

import android.content.Context
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.BASE_API_OK_HTTP_CLIENT_CONNECTION_TIMEOUT_IN_SEC
import com.bmc.buenacocina.core.BASE_API_OK_HTTP_CLIENT_READ_TIMEOUT_IN_SEC
import com.bmc.buenacocina.core.BASE_API_OK_HTTP_CLIENT_WRITE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.core.PY_API_OK_HTTP_CLIENT_CONNECTION_TIMEOUT_IN_SEC
import com.bmc.buenacocina.core.PY_API_OK_HTTP_CLIENT_READ_TIMEOUT_IN_SEC
import com.bmc.buenacocina.core.PY_API_OK_HTTP_CLIENT_WRITE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.data.network.service.AlgoliaTokenService
import com.bmc.buenacocina.data.network.service.GetStreamChannelService
import com.bmc.buenacocina.data.network.service.GetStreamTokenService
import com.bmc.buenacocina.data.network.service.InsightService
import com.bmc.buenacocina.data.network.service.ProductReviewAnalyzedService
import com.bmc.buenacocina.data.network.service.StoreReviewAnalyzedService
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @BaseApi
    @Provides
    fun provideBaseApiHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
    }

    @PyApi
    @Provides
    fun providePyApiHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
    }

    @BaseApi
    @Provides
    fun provideBaseApiOkHttpClient(
        @BaseApi interceptor: Interceptor
    ): OkHttpClient {
        val connectionTimeout =
            Duration.ofSeconds(BASE_API_OK_HTTP_CLIENT_CONNECTION_TIMEOUT_IN_SEC)
        val writeTimeout = Duration.ofSeconds(BASE_API_OK_HTTP_CLIENT_WRITE_TIMEOUT_IN_SEC)
        val readTimeout = Duration.ofSeconds(BASE_API_OK_HTTP_CLIENT_READ_TIMEOUT_IN_SEC)
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(connectionTimeout)
            .writeTimeout(writeTimeout)
            .readTimeout(readTimeout)
            .build()
    }

    @PyApi
    @Provides
    fun providePyApiOkHttpClient(
        @PyApi interceptor: Interceptor
    ): OkHttpClient {
        val connectionTimeout = Duration.ofSeconds(PY_API_OK_HTTP_CLIENT_CONNECTION_TIMEOUT_IN_SEC)
        val writeTimeout = Duration.ofSeconds(PY_API_OK_HTTP_CLIENT_WRITE_TIMEOUT_IN_SEC)
        val readTimeout = Duration.ofSeconds(PY_API_OK_HTTP_CLIENT_READ_TIMEOUT_IN_SEC)
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(connectionTimeout)
            .writeTimeout(writeTimeout)
            .readTimeout(readTimeout)
            .build()
    }

    @Singleton
    @BaseApi
    @Provides
    fun provideBaseApiRetrofit(
        @BaseApi okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit {
        val baseUrl = context.getString(R.string.base_api_server_url)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())  // Sandwich integration
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @PyApi
    @Provides
    fun providePyApiRetrofit(
        @PyApi okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit {
        val baseUrl = context.getString(R.string.py_api_server_url)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())  // Sandwich integration
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideGetStreamTokenService(@BaseApi retrofit: Retrofit): GetStreamTokenService {
        return retrofit.create(GetStreamTokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetStreamChannelService(@BaseApi retrofit: Retrofit): GetStreamChannelService {
        return retrofit.create(GetStreamChannelService::class.java)
    }

    @Provides
    @Singleton
    fun provideAlgoliaTokenService(@BaseApi retrofit: Retrofit): AlgoliaTokenService {
        return retrofit.create(AlgoliaTokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreReviewAnalyzedService(@PyApi retrofit: Retrofit): StoreReviewAnalyzedService {
        return retrofit.create(StoreReviewAnalyzedService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductReviewAnalyzedService(@PyApi retrofit: Retrofit): ProductReviewAnalyzedService {
        return retrofit.create(ProductReviewAnalyzedService::class.java)
    }

    @Provides
    @Singleton
    fun provideInsightService(@PyApi retrofit: Retrofit): InsightService {
        return retrofit.create(InsightService::class.java)
    }
}