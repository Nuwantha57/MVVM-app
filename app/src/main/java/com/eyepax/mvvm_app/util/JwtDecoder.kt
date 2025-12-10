package com.eyepax.mvvm_app.util

import android.util.Log
import com.auth0.android.jwt.JWT
import com.eyepax.mvvm_app.model.UserInfo

object JwtDecoder {

    private const val TAG = "JwtDecoder"

    fun decodeIdToken(idToken: String): UserInfo? {
        return try {
            val jwt = JWT(idToken)

            // Extract claims from ID token
            val sub = jwt.getClaim("sub").asString() ?: "unknown"
            val email = jwt.getClaim("email").asString() ?: "unknown@example.com"
            val name = jwt.getClaim("name").asString()
                ?: jwt.getClaim("given_name").asString()
                ?: jwt.getClaim("cognito:username").asString()
                ?: "User"
            val username = jwt.getClaim("cognito:username").asString() ?: email

            Log.d(TAG, "Decoded ID Token:")
            Log.d(TAG, "Sub (userId): $sub")
            Log.d(TAG, "Email: $email")
            Log.d(TAG, "Name: $name")
            Log.d(TAG, "Username: $username")

            UserInfo(
                userId = sub,
                email = email,
                name = name,
                username = username
            )

        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode ID token", e)
            null
        }
    }
}
