package com.eyepax.mvvm_app.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {

    private val _signUpResult = MutableLiveData<String>()
    val signUpResult: LiveData<String> get() = _signUpResult

    fun register(username: String, email: String, password: String) {
        // Validate empty fields
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _signUpResult.value = "All fields are required"
            return
        }

        // Validate email format
        if (!isValidEmail(email)) {
            _signUpResult.value = "Invalid email format"
            return
        }

        // Validate password length (optional)
        if (password.length < 6) {
            _signUpResult.value = "Password must be at least 6 characters"
            return
        }

        // For now, simulate success
        // Here, you could add validation or save to local database
        _signUpResult.value = "Registration successful! Please proceed to Sign In."
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
