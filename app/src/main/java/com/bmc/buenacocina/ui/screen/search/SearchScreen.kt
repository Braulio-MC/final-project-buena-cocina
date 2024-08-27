package com.bmc.buenacocina.ui.screen.search

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.domain.model.SearchResultDomain
import com.bmc.buenacocina.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchScreenContent(
        uiState = viewModel.state.value,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit
) {
    val onSearch: (String) -> Unit = {
        onIntent(SearchIntent.Search)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth(),
            query = uiState.searchQuery,
            onQueryChange = { query ->
                onIntent(SearchIntent.UpdateSearchQuery(query))
                // onSearch(query)
            },
            onSearch = onSearch,
            active = uiState.isActive,
            onActiveChange = { active ->
                onIntent(SearchIntent.UpdateIsActive(active))
            },
            placeholder = {
                Text(
                    text = "Busca tiendas o productos",
                    fontSize = 18.sp
                )
            },
//            leadingIcon = {
//                IconButton(
//                    onClick = {
//                        onNavBackButton()
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back navigation"
//                    )
//                }
//            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSearch(uiState.searchQuery)
                    },
                    enabled = uiState.searchQuery.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
            }
        ) {
            if (uiState.searchQuery.isNotEmpty()) {
                val hits = uiState.hits.collectAsLazyPagingItems()
                if (hits.loadState.refresh is LoadState.Loading) {

                } else if (hits.loadState.refresh is LoadState.Error) {
                    Toast.makeText(
                        LocalContext.current,
                        (hits.loadState.refresh as LoadState.Error).error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            count = hits.itemCount,
                            key = hits.itemKey { item ->
                                item.id
                            }
                        ) { index ->
                            val item = hits[index]
                            if (item != null) {
                                SearchHitItem(
                                    hit = item
                                ) {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

