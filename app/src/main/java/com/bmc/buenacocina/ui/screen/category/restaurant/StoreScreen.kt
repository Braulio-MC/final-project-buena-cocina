package com.bmc.buenacocina.ui.screen.category.restaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bmc.buenacocina.R
import com.bmc.buenacocina.domain.model.StoreDomain
import com.bmc.buenacocina.ui.viewmodel.RestaurantCategoryViewModel

data class ProductCategory(
    val title: String,
    val drawable: Int
)

val productCategories = listOf(
    ProductCategory("Hamburguesa", R.drawable.hamburger_category),
    ProductCategory("Pizza", R.drawable.pizza_category),
    ProductCategory("Dulces", R.drawable.candy_category),
    ProductCategory("Sushi", R.drawable.sushi_category),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: RestaurantCategoryViewModel = hiltViewModel(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onSearchBarButton: () -> Unit,
    onStore: (String) -> Unit,
    onBackButton: () -> Unit
) {
    val storesExplore = viewModel.storesExplore.collectAsLazyPagingItems()

    StoreScreenContent(
        windowSizeClass = windowSizeClass,
        storesExplore = storesExplore,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onSearchBarButton = onSearchBarButton,
        onStore = onStore,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreenContent(
    windowSizeClass: WindowSizeClass,
    storesExplore: LazyPagingItems<StoreDomain>,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (StoreIntent) -> Unit,
    onSearchBarButton: () -> Unit,
    onStore: (String) -> Unit,
    onBackButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.restaurant_cat_screen_title),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onBackButton() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_navigation_button)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            Button(
                onClick = {
                    onSearchBarButton()
                },
                modifier = Modifier
                    .padding(20.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.search_button_text),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_bar_icon_content_desc),
                        modifier = Modifier
                            .size(35.dp)
                    )
                }
            }
            LazyRow(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
            ) {
                items(productCategories) { item ->
                    ProductCategoryItem(
                        productCategory = item,
                        onClick = { }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.restaurant_cat_stores_favorite),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                LazyRow {

                }
            }
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.restaurant_cat_stores_best_rated),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                LazyRow {

                }
            }
            Text(
                text = stringResource(id = R.string.restaurant_cat_stores_explore),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth()
            )
            when (storesExplore.loadState.refresh) {
                is LoadState.Error -> {

                }

                LoadState.Loading -> {

                }

                is LoadState.NotLoading -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(5.dp)
                            .heightIn(max = 1000.dp)
                            .nestedScroll(connection = object : NestedScrollConnection {
                                override fun onPreScroll(
                                    available: Offset,
                                    source: NestedScrollSource
                                ): Offset {
                                    if (scrollState.canScrollForward && available.y < 0) {
                                        val consumed = scrollState.dispatchRawDelta(-available.y)
                                        return Offset(x = 0f, y = -consumed)
                                    }
                                    return Offset.Zero
                                }
                            })
                    ) {
                        items(
                            count = storesExplore.itemCount,
                            key = storesExplore.itemKey { item -> item.id }
                        ) { index ->
                            val store = storesExplore[index]
                            if (store != null) {
                                StoreItem(
                                    store = store,
                                    onClick = onStore
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCategoryItem(
    productCategory: ProductCategory,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(3.dp)
            .size(100.dp)
            .minimumInteractiveComponentSize()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = productCategory.drawable),
                contentDescription = stringResource(id = R.string.restaurant_cat_specific_img_content_desc),
                modifier = Modifier
                    .padding(5.dp)
                    .size(50.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = productCategory.title,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
