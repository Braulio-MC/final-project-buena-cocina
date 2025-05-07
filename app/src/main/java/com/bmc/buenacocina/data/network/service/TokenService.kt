package com.bmc.buenacocina.data.network.service

import android.util.Log
import com.bmc.buenacocina.core.USER_COLLECTION_NAME
import com.bmc.buenacocina.core.USER_SUB_COLLECTION_TOKEN
import com.bmc.buenacocina.domain.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class TokenService @Inject constructor(
    private val userService: UserService,
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
    private val messaging: FirebaseMessaging
) {
    private fun callPushNotificationCreate(
        fParams: HashMap<String, String>,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        functions
            .getHttpsCallable("pushNotification-create")
            .call(fParams)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<*, *>
                    when {
                        result == null -> {
                            onFailure(
                                "Unknown error",
                                "Server did not return a response"
                            )
                        }

                        else -> {
                            val message = result["message"] as? String ?: ""
                            onSuccess(message)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseFunctionsException) {
                        val message = exception.message
                            ?: "Push notification token create error"
                        val details =
                            exception.details?.toString() ?: ""
                        onFailure(message, details)
                    } else {
                        onFailure(
                            "Unexpected error",
                            "Unknown error"
                        )
                    }
                }
            }
    }

    private fun callPushNotificationRemove(
        fParams: HashMap<String, String>,
        onSuccess: (String, Int) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        functions
            .getHttpsCallable("pushNotification-remove")
            .call(fParams)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.data as? Map<*, *>
                    when {
                        result == null -> {
                            onFailure(
                                "Unknown error",
                                "Server did not return a response"
                            )
                        }

                        else -> {
                            val message = result["message"] as? String ?: ""
                            val response = result["data"] as? Map<*, *>
                            val processedCount = response?.get("processedCount") as? Int ?: 0
                            onSuccess(message, processedCount)
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseFunctionsException) {
                        val message = exception.message
                            ?: "Push notification token remove error"
                        val details =
                            exception.details?.toString() ?: ""
                        onFailure(message, details)
                    } else {
                        onFailure(
                            "Unexpected error",
                            "Unknown error"
                        )
                    }
                }
            }
    }

    suspend fun create(
        token: String? = null,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        when (val result = userService.getUserId()) {
            is Result.Error -> {
                Log.e("TokenService", "Error on get user id: ${result.error}")
            }

            is Result.Success -> {
                if (token == null) {
                    messaging.token.addOnSuccessListener { tokenListener ->
                        this.exists(
                            userId = result.data,
                            token = tokenListener,
                            onSuccess = { exists ->
                                if (!exists) {
                                    val fParams = hashMapOf(
                                        "userId" to result.data,
                                        "token" to tokenListener
                                    )
                                    callPushNotificationCreate(
                                        fParams = fParams,
                                        onSuccess = onSuccess,
                                        onFailure = onFailure
                                    )
                                }
                            },
                            onFailure = onFailure
                        )
                    }.addOnFailureListener { _ ->
                        onFailure(
                            "Unexpected error",
                            "An error occurred while getting the token"
                        )
                    }
                } else {
                    this.exists(
                        userId = result.data,
                        token = token,
                        onSuccess = { exists ->
                            if (!exists) {
                                val fParams = hashMapOf(
                                    "userId" to result.data,
                                    "token" to token
                                )
                                callPushNotificationCreate(
                                    fParams = fParams,
                                    onSuccess = onSuccess,
                                    onFailure = onFailure
                                )
                            }
                        },
                        onFailure = onFailure
                    )
                }
            }
        }
    }

    private fun exists(
        userId: String?,
        token: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        if (userId != null) {
            val q = firestore.collection(USER_COLLECTION_NAME)
                .document(userId)
                .collection(USER_SUB_COLLECTION_TOKEN)
                .whereEqualTo("token", token)
                .limit(1)
                .get()
            q.addOnSuccessListener {
                onSuccess(it.size() > 0)
            }.addOnFailureListener { _ ->
                onFailure("Unexpected error", "An error occurred while checking if token exists")
            }
        } else {
            onFailure("Unexpected error", "User id is null")
        }
    }

    suspend fun remove(
        token: String? = null,
        onSuccess: (String, Int) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        when (val result = userService.getUserId()) {
            is Result.Error -> {

            }

            is Result.Success -> {
                if (token == null) {
                    messaging.token.addOnSuccessListener { currentToken ->
                        val fParams = hashMapOf(
                            "userId" to result.data,
                            "token" to currentToken
                        )
                        callPushNotificationRemove(
                            fParams = fParams,
                            onSuccess = onSuccess,
                            onFailure = onFailure
                        )
                    }.addOnFailureListener { e ->
                        onFailure(
                            "Unexpected error",
                            "An error occurred while getting the token"
                        )
                    }
                } else {
                    val fParams = hashMapOf(
                        "userId" to result.data,
                        "token" to token
                    )
                    callPushNotificationRemove(
                        fParams = fParams,
                        onSuccess = onSuccess,
                        onFailure = onFailure
                    )
                }
            }
        }
    }
}