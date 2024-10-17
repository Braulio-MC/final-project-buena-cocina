package com.bmc.buenacocina.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.bmc.buenacocina.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: HomeViewModel = hiltViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    profileBottomSheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onSearchBarButton: () -> Unit,
    onStoreCategoryButton: () -> Unit,
    onLogoutButton: (Boolean) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    var showProfileBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    if (showProfileBottomSheet) {
        ProfileBottomSheet(
            uiState = uiState.value,
            sheetState = profileBottomSheetState,
            onStartLogout = viewModel::startLogout,
            onLogoutButton = {
                onLogoutButton(it)
                showProfileBottomSheet = false
            },
            onDismissRequest = { showProfileBottomSheet = false }
        )
    }

    HomeScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        scrollState = scrollState,
        onProfileImage = { showProfileBottomSheet = true },
        onSearchBarButton = onSearchBarButton,
        onStoreCategoryButton = onStoreCategoryButton
    )
}

@Composable
fun HomeScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: HomeUiState,
    scrollState: ScrollState,
    onProfileImage: () -> Unit,
    onSearchBarButton: () -> Unit,
    onStoreCategoryButton: () -> Unit
) {
    val userName = uiState.userProfile?.name ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (userImg, searchBar) = createRefs()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(245.dp)
                    .constrainAs(userImg) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
            )
            Row(
                modifier = Modifier
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(start = 14.dp)
                        .weight(0.7f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Bienvenido",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W400,
                    )
                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 14.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { onProfileImage() }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uiState.userProfile?.pictureURL)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .constrainAs(searchBar) {
                        top.linkTo(userImg.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(userImg.bottom)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        onSearchBarButton()
                    },
                    modifier = Modifier
                        .minimumInteractiveComponentSize(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Buscar tiendas o productos",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                }
            }
        }
        Text(
            text = "Explora las categorias",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable { onStoreCategoryButton() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = painterResource(id = R.drawable.restaurant_category),
                    contentDescription = "Restaurant category logo",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(50.dp)
                )
                Text(
                    text = "Restaurante",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(5.dp)
                )
            }
        }
        Text(
            text = "Productos mejor calificados",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        )
//        LazyColumn(
//            modifier = Modifier
//                .heightIn(max = 1000.dp)
//            // .nestedScroll()  // Nested scroll for best-rated products
//        ) {
//            items(10) { index ->
//
//            }
//        }
    }
}