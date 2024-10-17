package com.bmc.buenacocina.ui.screen.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.auth0.android.result.UserProfile
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginButton: (Boolean, UserProfile?) -> Unit
) {
    val netState = viewModel.netState.collectAsStateWithLifecycle()

    var isLoginButtonEnabled by remember {
        mutableStateOf(true)
    }

    LoginScreenContent(
        windowSizeClass = windowSizeClass,
        netState = netState.value,
        onLoginButton = onLoginButton,
        isLoginButtonEnabled = isLoginButtonEnabled,
        onLoginButtonChanged = { enabled ->
            isLoginButtonEnabled = enabled
        },
        onStartLogin = viewModel::startLogin
    )
}

@Composable
fun LoginScreenContent(
    windowSizeClass: WindowSizeClass,
    netState: NetworkStatus,
    onLoginButton: (Boolean, UserProfile?) -> Unit,
    isLoginButtonEnabled: Boolean,
    onLoginButtonChanged: (Boolean) -> Unit,
    onStartLogin: (Context, () -> Unit, (UserProfile) -> Unit) -> Unit
) {
    val currentContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.logo_transparent_background
            ),
            contentDescription = "Application main logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(350.dp)
                .padding(bottom = 30.dp)
                .weight(3f)
        )
        Card(
            shape = RoundedCornerShape(
                15.dp
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            ),
            modifier = Modifier
                .size(350.dp, 150.dp)
                .weight(1.5f)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    fontSize = 19.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    text = "Crea una cuenta o inicia sesion en Buena Cocina para comenzar"
                )
                Button(
                    onClick = {
                        onLoginButtonChanged(false)
                        onStartLogin(currentContext, {
                            onLoginButtonChanged(true)
                            onLoginButton(false, null)
                        }, { userProfile ->
                            onLoginButton(true, userProfile)
                        })
                    },
                    enabled = isLoginButtonEnabled,
                    modifier = Modifier
                        .size(250.dp, 50.dp)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        fontSize = 17.sp,
                        text = "Comenzar"
                    )
                }
            }
        }
        Text(
            fontSize = 15.sp,
            maxLines = 2,
            text = "Seras redirigido para crear una cuenta o iniciar sesion a traves de un sistema IAM",
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .size(350.dp)
                .padding(top = 30.dp)
                .weight(2f)
        )
    }
}