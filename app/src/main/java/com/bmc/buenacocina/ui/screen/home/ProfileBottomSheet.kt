package com.bmc.buenacocina.ui.screen.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    uiState: HomeUiState,
    sheetState: SheetState,
    onStartLogout: (Context, () -> Unit, () -> Unit) -> Unit,
    onLogoutButton: (Boolean) -> Unit,
    onStoreFavoritesButton: () -> Unit,
    onProductFavoritesButton: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var isLogoutButtonEnabled by remember {
        mutableStateOf(true)
    }

    ProfileBottomSheetContent(
        uiState = uiState,
        sheetState = sheetState,
        isLogoutButtonEnabled = isLogoutButtonEnabled,
        onLogoutButtonChanged = { enabled ->
            isLogoutButtonEnabled = enabled
        },
        onStartLogout = onStartLogout,
        onLogoutButton = onLogoutButton,
        onStoreFavoritesButton = onStoreFavoritesButton,
        onProductFavoritesButton = onProductFavoritesButton,
        onDismissRequest = onDismissRequest
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheetContent(
    uiState: HomeUiState,
    sheetState: SheetState,
    isLogoutButtonEnabled: Boolean,
    onLogoutButtonChanged: (Boolean) -> Unit,
    onStartLogout: (Context, () -> Unit, () -> Unit) -> Unit,
    onLogoutButton: (Boolean) -> Unit,
    onStoreFavoritesButton: () -> Unit,
    onProductFavoritesButton: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val currentContext = LocalContext.current

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.userProfile != null) {
                val userName = uiState.userProfile.name ?: ""
                val userEmail = uiState.userProfile.email ?: ""
                val isEmailVerified = uiState.userProfile.isEmailVerified ?: false
                val isEmailVerifiedText = if (isEmailVerified) "Si" else "No"

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier
                            .size(100.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uiState.userProfile.pictureURL)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Text(
                            text = userName,
                            textAlign = TextAlign.Start,
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Correo electronico",
                                textAlign = TextAlign.Start,
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Light,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                            )
                            Text(
                                text = userEmail,
                                textAlign = TextAlign.End,
                                color = Color.DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1.4f)
                                    .padding(end = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Correo verificado",
                                textAlign = TextAlign.Start,
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Light,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                            )
                            Text(
                                text = isEmailVerifiedText,
                                textAlign = TextAlign.End,
                                color = Color.DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1.4f)
                                    .padding(end = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Favoritos",
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(top = 5.dp),
                            thickness = 2.dp,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(3.dp)
                                    .clickable {
                                        onDismissRequest()
                                        onStoreFavoritesButton()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tiendas",
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            VerticalDivider(
                                modifier = Modifier
                                    .fillMaxHeight(),
                                thickness = 2.dp
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(3.dp)
                                    .clickable {
                                        onDismissRequest()
                                        onProductFavoritesButton()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Productos",
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    onLogoutButtonChanged(false)
                    onStartLogout(currentContext, {  // On logout error
                        onLogoutButtonChanged(true)
                        onLogoutButton(false)
                    }, {  // On logout success
                        onLogoutButton(true)
                    })
                },
                enabled = isLogoutButtonEnabled,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .padding(15.dp)
                    .size(180.dp, 50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Cerrar sesion",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}