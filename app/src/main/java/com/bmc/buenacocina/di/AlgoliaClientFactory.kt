package com.bmc.buenacocina.di

import com.algolia.client.api.SearchClient

interface AlgoliaClientFactory {
    fun create(scopedSecuredApiKey: String): SearchClient
}