package com.bmc.buenacocina.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bmc.buenacocina.domain.repository.SearchRepository
import com.bmc.buenacocina.ui.screen.search.SearchIntent
import com.bmc.buenacocina.ui.screen.search.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _state = mutableStateOf(SearchUiState())
    val state: State<SearchUiState> = _state

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            SearchIntent.Search -> {
                search()
            }

            is SearchIntent.UpdateSearchQuery -> {
                _state.value = state.value.copy(searchQuery = intent.searchQuery)
            }

            is SearchIntent.UpdateIsActive -> {
                _state.value = state.value.copy(isActive = intent.isActive)
            }
        }
    }

    private fun search() {
//        val hits = searchRepository.search(
//            query = state.value.searchQuery
//        ).cachedIn(viewModelScope)
//        _state.value = state.value.copy(hits = hits)
    }
}