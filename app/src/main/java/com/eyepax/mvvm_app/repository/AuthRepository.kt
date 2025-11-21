package com.eyepax.mvvm_app.repository

import android.util.Log
import com.eyepax.mvvm_app.model.LoginRequest
import com.eyepax.mvvm_app.model.LoginResponse
import com.eyepax.mvvm_app.network.RetrofitClient
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository {

    private val apiService = RetrofitClient.apiService
    private val TAG = "AuthRepository"

    suspend fun login(username: String, password: String): Resource<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting login for username: $username")

                val request = LoginRequest(username, password)
                val response: Response<LoginResponse> = apiService.login(request)

                Log.d(TAG, "Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d(TAG, "Login successful: ${body.message}")
                    Resource.Success(body)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    Log.e(TAG, "Login failed: $errorMessage")
                    Resource.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login", e)
                Resource.Error(e.message ?: "An error occurred")
            }
        }
    }
}
