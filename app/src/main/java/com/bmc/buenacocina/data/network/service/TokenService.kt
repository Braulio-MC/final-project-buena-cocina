package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.core.USER_COLLECTION_NAME
import com.bmc.buenacocina.core.USER_SUB_COLLECTION_TOKEN
import com.bmc.buenacocina.domain.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class TokenService @Inject constructor(
    private val userService: UserService,
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
    private val messaging: FirebaseMessaging
) {
    suspend fun create(
        token: String,
        onSuccess: (Any?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        when (val result = userService.getUserId()) {
            is Result.Error -> {

            }

            is Result.Success -> {
                val fParams = hashMapOf(
                    "userId" to result.data,
                    "token" to token
                )
                functions
                    .getHttpsCallable("pushNotification-create")
                    .call(fParams)
                    .addOnSuccessListener { response ->
                        onSuccess(response.getData())
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
        }
    }

    fun exists(
        userId: String?,
        token: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
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
            }.addOnFailureListener { e ->
                onFailure(e)
            }
        } else {
            onFailure(Exception("User id is null")) // Custom exception here
        }
    }

    suspend fun remove(
        token: String? = null,
        onSuccess: (Any?) -> Unit,
        onFailure: (Exception) -> Unit
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
                        functions
                            .getHttpsCallable("pushNotification-remove")
                            .call(fParams)
                            .addOnSuccessListener { response ->
                                onSuccess(response.getData())
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }.addOnFailureListener { e ->
                        onFailure(e)
                    }
                } else {
                    val fParams = hashMapOf(
                        "userId" to result.data,
                        "token" to token
                    )
                    functions
                        .getHttpsCallable("pushNotification-remove")
                        .call(fParams)
                        .addOnSuccessListener { response ->
                            onSuccess(response.getData())
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                }
            }
        }
    }
}