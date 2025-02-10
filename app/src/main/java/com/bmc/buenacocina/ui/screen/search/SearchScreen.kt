package com.bmc.buenacocina.ui.screen.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bmc.buenacocina.common.SearchableTypes
import com.bmc.buenacocina.domain.model.ProductSearchDomain
import com.bmc.buenacocina.domain.model.StoreSearchDomain
import com.bmc.buenacocina.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onStoreHitItemClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreenContent(
        uiState = uiState.value,
        onIntent = viewModel::onIntent,
        onStoreHitItemClick = onStoreHitItemClick,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    onStoreHitItemClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = uiState.searchQuery,
                                onQueryChange = { newQuery ->
                                    onIntent(SearchIntent.UpdateSearchQuery(newQuery))
                                },
                                onSearch = { onIntent(SearchIntent.Search) },
                                expanded = false,
                                onExpandedChange = {},
                                enabled = true,
                                placeholder = {
                                    Text(
                                        text = "Busca tiendas o productos",
                                        maxLines = 1,
                                        fontSize = 16.sp
                                    )
                                },
                                leadingIcon = {
                                    IconButton(onClick = { onBackButton() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back navigation"
                                        )
                                    }
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { onIntent(SearchIntent.Search) },
                                        enabled = uiState.searchQuery.isNotEmpty()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search button"
                                        )
                                    }
                                }
                            )
                        },
                        expanded = false,
                        onExpandedChange = {}
                    ) {}
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(items = uiState.hits) { hit ->
                when (hit.type) {
                    SearchableTypes.PRODUCTS -> {
                        val product = hit as ProductSearchDomain
                        SearchProductItem(
                            hit = product,
                            onClick = { }
                        )
                    }

                    SearchableTypes.STORES -> {
                        val store = hit as StoreSearchDomain
                        SearchStoreItem(
                            hit = store,
                            onClick = onStoreHitItemClick
                        )
                    }
                }
            }
        }
    }
}

