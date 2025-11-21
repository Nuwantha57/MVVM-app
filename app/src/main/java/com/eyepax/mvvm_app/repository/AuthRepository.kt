package com.eyepax.mvvm_app.repository

import android.util.Log
import com.eyepax.mvvm_app.model.*
import com.eyepax.mvvm_app.network.RetrofitClient
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository {

    private val apiService = RetrofitClient.apiService
    private val TAG = "AuthRepository"

    // Existing login method
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

    // Sign Up method
    suspend fun signUp(
        username: String,
        password: String,
        email: String,
        name: String,
        phoneNumber: String
    ): Resource<SignUpResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Attempting sign up for username: $username")

                val request = SignUpRequest(
                    username = username,
                    password = password,
                    email = email,
                    name = name,
                    phoneNumber = phoneNumber
                )

                val response: Response<SignUpResponse> = apiService.signUp(request)

                Log.d(TAG, "Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d(TAG, "Sign up successful: ${body.message}")
                    Resource.Success(body)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    Log.e(TAG, "Sign up failed: $errorMessage")
                    Resource.Error("Sign up failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during sign up", e)
                Resource.Error(e.message ?: "An error occurred")
            }
        }
    }
}
