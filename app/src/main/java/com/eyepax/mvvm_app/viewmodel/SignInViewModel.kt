package com.eyepax.mvvm_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyepax.mvvm_app.model.LoginResponse
import com.eyepax.mvvm_app.model.UserInfo
import com.eyepax.mvvm_app.repository.AuthRepository
import com.eyepax.mvvm_app.util.JwtDecoder
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.launch

data class CompleteUserData(
    val userInfo: UserInfo,
    val accessToken: String,
    val idToken: String?,
    val refreshToken: String?
)

class SignInViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val TAG = "SignInViewModel"

    private val _loginResult = MutableLiveData<Resource<LoginResponse>>()
    val loginResult: LiveData<Resource<LoginResponse>> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Complete user data with decoded info
    private val _completeUserData = MutableLiveData<CompleteUserData>()
    val completeUserData: LiveData<CompleteUserData> get() = _completeUserData

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = Resource.Error("Username and password cannot be empty")
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting login process...")

                val result = repository.login(username, password)

                when (result) {
                    is Resource.Success -> {
                        val response = result.data
                        if (response != null && response.success) {
                            Log.d(TAG, "Login successful")

                            // Decode ID token to get user info
                            val userInfo = response.idToken?.let { idToken ->
                                JwtDecoder.decodeIdToken(idToken)
                            }

                            if (userInfo != null) {
                                Log.d(TAG, "User info decoded successfully")
                                _completeUserData.value = CompleteUserData(
                                    userInfo = userInfo,
                                    accessToken = response.accessToken ?: "",
                                    idToken = response.idToken,
                                    refreshToken = response.refreshToken
                                )
                                _loginResult.value = Resource.Success(response)
                            } else {
                                Log.e(TAG, "Failed to decode user info from ID token")
                                _loginResult.value = Resource.Error("Failed to decode user information")
                            }
                        } else {
                            Log.e(TAG, "Login failed: ${response?.message}")
                            _loginResult.value = Resource.Error(response?.message ?: "Login failed")
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Login error: ${result.message}")
                        _loginResult.value = Resource.Error(result.message ?: "Login failed")
                    }
                    is Resource.Loading -> {
                        // Already showing loading
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login", e)
                _loginResult.value = Resource.Error("An error occurred: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
