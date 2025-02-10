package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_PRODUCTS_INDEX
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_STORES_INDEX
import com.bmc.buenacocina.domain.repository.SearchRepository
import com.bmc.buenacocina.ui.screen.search.SearchIntent
import com.bmc.buenacocina.ui.screen.search.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            SearchIntent.Search -> {
                search()
            }

            is SearchIntent.UpdateSearchQuery -> {
                _uiState.update { currentState ->
                    currentState.copy(searchQuery = intent.searchQuery)
                }
            }
        }
    }

    private fun search() {
        searchRepository.searchMultiIndex(
            query = _uiState.value.searchQuery,
            indexNames = listOf(
                ALGOLIA_SEARCH_PRODUCTS_INDEX,
                ALGOLIA_SEARCH_STORES_INDEX
            )
        ).onStart {
            _uiState.update { currentState ->
                currentState.copy(isLoadingHits = true)
            }
        }.onEach { hits ->
            _uiState.update { currentState ->
                currentState.copy(isLoadingHits = false, hits = hits)
            }
        }.catch { e ->

        }.launchIn(viewModelScope)
    }
}