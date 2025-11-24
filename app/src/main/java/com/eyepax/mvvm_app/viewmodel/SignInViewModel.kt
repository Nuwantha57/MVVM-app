package com.eyepax.mvvm_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyepax.mvvm_app.repository.AuthRepository
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val TAG = "SignInViewModel"

    // Login result LiveData
    private val _loginResult = MutableLiveData<Pair<Boolean, String>>()
    val loginResult: LiveData<Pair<Boolean, String>> get() = _loginResult

    // Loading state LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Access token LiveData (for storing JWT)
    private val _accessToken = MutableLiveData<String?>()
    val accessToken: LiveData<String?> get() = _accessToken

    fun login(username: String, password: String) {
        // Validation
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = Pair(false, "Username and password cannot be empty")
            return
        }

        // Show loading
        _isLoading.value = true

        // Launch coroutine in ViewModel scope
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting login process...")

                // Call repository
                val result = repository.login(username, password)

                // Handle result
                when (result) {
                    is Resource.Success -> {
                        val response = result.data
                        if (response != null && response.success) {
                            Log.d(TAG, "Login successful")
                            _accessToken.value = response.accessToken
                            _loginResult.value = Pair(true, response.message)
                        } else {
                            Log.e(TAG, "Login failed: ${response?.message}")
                            _loginResult.value = Pair(false, response?.message ?: "Login failed")
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Login error: ${result.message}")
                        _loginResult.value = Pair(false, result.message ?: "Login failed")
                    }
                    is Resource.Loading -> {
                        // Already showing loading
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login", e)
                _loginResult.value = Pair(false, "An error occurred: ${e.message}")
            } finally {
                // Hide loading
                _isLoading.value = false
            }
        }
    }
}
