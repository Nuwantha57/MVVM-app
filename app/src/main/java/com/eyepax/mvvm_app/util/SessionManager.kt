package com.eyepax.mvvm_app.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_ID_TOKEN = "id_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Save session after login
    fun saveUserSession(
        accessToken: String,
        refreshToken: String?,
        idToken: String?,
        userId: String,
        email: String,
        name: String
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_ID_TOKEN, idToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Get saved tokens
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getIdToken(): String? = prefs.getString(KEY_ID_TOKEN, null)

    // Get saved user info
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    // Check if user is logged in
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    // Clear session on logout
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
