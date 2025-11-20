package com.eyepax.mvvm_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

    // LiveData to hold login success/failure status and message
    private val _loginResult = MutableLiveData<Pair<Boolean, String>>()
    val loginResult: LiveData<Pair<Boolean, String>> get() = _loginResult

    // Test data for username and password
    private val validUsername = "testuser"
    private val validPassword = "password123"

    // Validate input and check credentials
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = Pair(false, "Username and password cannot be empty")
        } else if (username == validUsername && password == validPassword) {
            _loginResult.value = Pair(true, "Login successful")
        } else {
            _loginResult.value = Pair(false, "Invalid username or password")
        }
    }
}
