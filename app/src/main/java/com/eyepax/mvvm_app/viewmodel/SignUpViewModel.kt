package com.eyepax.mvvm_app.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyepax.mvvm_app.repository.AuthRepository
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val TAG = "SignUpViewModel"

    // Sign up result LiveData
    private val _signUpResult = MutableLiveData<Pair<Boolean, String>>()
    val signUpResult: LiveData<Pair<Boolean, String>> get() = _signUpResult

    // Loading state LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun signUp(
        username: String,
        password: String,
        confirmPassword: String,
        email: String,
        name: String,
        phoneNumber: String
    ) {
        // Validation
        val validationError = validateInputs(
            username, password, confirmPassword, email, name, phoneNumber
        )

        if (validationError != null) {
            _signUpResult.value = Pair(false, validationError)
            return
        }

        // Show loading
        _isLoading.value = true

        // Launch coroutine
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting sign up process...")

                // Call repository
                val result = repository.signUp(
                    username = username,
                    password = password,
                    email = email,
                    name = name,
                    phoneNumber = phoneNumber
                )

                // Handle result
                when (result) {
                    is Resource.Success -> {
                        val response = result.data
                        if (response != null && response.success) {
                            Log.d(TAG, "Sign up successful")
                            _signUpResult.value = Pair(true, response.message)
                        } else {
                            Log.e(TAG, "Sign up failed: ${response?.message}")
                            _signUpResult.value = Pair(false, response?.message ?: "Sign up failed")
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Sign up error: ${result.message}")
                        _signUpResult.value = Pair(false, result.message ?: "Sign up failed")
                    }
                    is Resource.Loading -> {
                        // Already showing loading
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during sign up", e)
                _signUpResult.value = Pair(false, "An error occurred: ${e.message}")
            } finally {
                // Hide loading
                _isLoading.value = false
            }
        }
    }

    private fun validateInputs(
        username: String,
        password: String,
        confirmPassword: String,
        email: String,
        name: String,
        phoneNumber: String
    ): String? {
        return when {
            username.isBlank() -> "Username is required"
            username.length < 3 -> "Username must be at least 3 characters"
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter"
            !password.matches(Regex(".*[a-z].*")) -> "Password must contain at least one lowercase letter"
            !password.matches(Regex(".*\\d.*")) -> "Password must contain at least one number"
            !password.matches(Regex(".*[@#\$%^&+=!].*")) -> "Password must contain at least one special character"
            password != confirmPassword -> "Passwords do not match"
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            name.isBlank() -> "Name is required"
            phoneNumber.isBlank() -> "Phone number is required"
            !phoneNumber.matches(Regex("^\\+?[1-9]\\d{1,14}\$")) -> "Invalid phone number format (use international format: +1234567890)"
            else -> null
        }
    }
}
