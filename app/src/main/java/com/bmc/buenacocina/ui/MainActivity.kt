package com.bmc.buenacocina.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.bmc.buenacocina.R
import com.bmc.buenacocina.data.network.model.GetStreamUserCredentials
import com.bmc.buenacocina.data.preferences.PreferencesService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.mapper.asUiText
import com.bmc.buenacocina.domain.repository.ChatRepository
import com.bmc.buenacocina.domain.repository.GetStreamTokenRepository
import com.bmc.buenacocina.domain.repository.TokenRepository
import com.bmc.buenacocina.domain.repository.UserRepository
import com.bmc.buenacocina.ui.navigation.graph.NavigationGraph
import com.bmc.buenacocina.ui.theme.BuenaCocinaTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory
import io.getstream.chat.android.models.User
import io.getstream.chat.android.models.querysort.QuerySortByField
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var messaging: FirebaseMessaging

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    lateinit var chatClient: ChatClient

    @Inject
    lateinit var preferencesService: PreferencesService

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var getStreamTokenRepository: GetStreamTokenRepository

    @Inject
    lateinit var getStreamChatRepository: ChatRepository

    private val channelViewModelFactory by lazy {
        ChannelViewModelFactory(
            chatClient = chatClient,
            QuerySortByField.descByName("last_updated")
        )
    }

//    private val channelViewModel: ChannelListViewModel by viewModels { channelViewModelFactory }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        connectUserToGetStream(splashScreen)
        FirebaseApp.initializeApp(this)
        setContent {
            BuenaCocinaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val windowSizeClass = calculateWindowSizeClass(activity = this)
                    NavigationGraph(
                        windowSizeClass = windowSizeClass,
                        channelViewModelFactory = channelViewModelFactory,
                        onUserAuthenticated = { userProfile ->
                            processFCMTokenCreation(userProfile?.getId(), splashScreen)
                        }
                    )
                }
            }
        }
        splashScreen.setKeepOnScreenCondition { false }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    private fun connectUserToGetStream(ss: SplashScreen) {
        val credentials = preferencesService.getUserCredentials()
        if (credentials != null) {
            lifecycleScope.launch {
                getStreamChatRepository.connectUser(
                    credentials,
                    onSuccess = {
                        ss.setKeepOnScreenCondition { false }
                    },
                    onFailure = { e ->
                        Log.e("MainActivity", "Error on connectUser with credentials: ${e.message}")
                        ss.setKeepOnScreenCondition { false }
                    }
                )
            }
        } else {
            lifecycleScope.launch {
                when (val resultUser = userRepository.getUserProfile()) {
                    is Result.Error -> {

                    }

                    is Result.Success -> {
                        val userId = resultUser.data.getId()
                        val userName = resultUser.data.name ?: resultUser.data.nickname
                        val image = resultUser.data.pictureURL

                        if (userId != null && userName != null && image != null) {
                            val id = userId.replace("|", "-")
                            when (val resultToken = getStreamTokenRepository.request(id)) {
                                is Result.Error -> {
                                    ss.setKeepOnScreenCondition { false }
                                    Log.e(
                                        "MainActivity",
                                        "Error on request token: ${
                                            resultToken.error.asUiText().asString(this@MainActivity)
                                        }"
                                    )
                                }

                                is Result.Success -> {
                                    val getStreamCredentials = GetStreamUserCredentials(
                                        apiKey = this@MainActivity.getString(R.string.get_stream_api_key),
                                        user = User(
                                            id = id,
                                            name = userName,
                                            image = image

                                        ),
                                        token = resultToken.data
                                    )
                                    preferencesService.saveUserCredentials(getStreamCredentials)
                                    getStreamChatRepository.connectUser(
                                        getStreamCredentials,
                                        onSuccess = { },
                                        onFailure = { e ->
                                            Log.e("LoginViewModel", "Error: ${e.message}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun processFCMTokenCreation(userId: String?, ss: SplashScreen) {
        ss.setKeepOnScreenCondition { true }
        messaging.token.addOnSuccessListener { token ->
            tokenRepository.exists(
                userId = userId,
                token = token,
                onSuccess = { exists ->
                    if (exists) {
                        ss.setKeepOnScreenCondition { false }
                    } else {
                        processFCMTokenDoesNotExist(
                            token = token,
                            onSuccess = {
                                ss.setKeepOnScreenCondition { false }
                            },
                            onFailure = { e ->
                                ss.setKeepOnScreenCondition { false }
                                e.printStackTrace()
                            }
                        )
                    }
                },
                onFailure = { e ->
                    ss.setKeepOnScreenCondition { false }
                    e.printStackTrace()
                }
            )
        }.addOnFailureListener { e ->
            ss.setKeepOnScreenCondition { false }
            e.printStackTrace()
        }
    }

    private fun processFCMTokenDoesNotExist(
        token: String,
        onSuccess: (Any?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        lifecycleScope.launch {
            tokenRepository.create(
                token = token,
                onSuccess = {
                    onSuccess(it)
                },
                onFailure = { e ->
                    onFailure(e)
                }
            )
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}