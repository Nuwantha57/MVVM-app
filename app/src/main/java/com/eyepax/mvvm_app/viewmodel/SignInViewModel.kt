package com.eyepax.mvvm_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyepax.mvvm_app.model.LoginResponse
import com.eyepax.mvvm_app.repository.AuthRepository
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val TAG = "SignInViewModel"

    // Login result LiveData - now includes full response
    private val _loginResult = MutableLiveData<Resource<LoginResponse>>()
    val loginResult: LiveData<Resource<LoginResponse>> get() = _loginResult

    // Loading state LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // User credentials for passing to Flutter
    private val _userCredentials = MutableLiveData<Pair<String, LoginResponse>>()
    val userCredentials: LiveData<Pair<String, LoginResponse>> get() = _userCredentials

    fun login(username: String, password: String) {
        // Validation
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = Resource.Error("Username and password cannot be empty")
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
                            _loginResult.value = Resource.Success(response)
                            _userCredentials.value = Pair(username, response)
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
                // Hide loading
                _isLoading.value = false
            }
        }
    }
}
