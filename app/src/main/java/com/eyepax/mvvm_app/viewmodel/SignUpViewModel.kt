package com.eyepax.mvvm_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {

    private val _signUpResult = MutableLiveData<String>()
    val signUpResult: LiveData<String> get() = _signUpResult

    fun register(username: String, email: String, password: String) {
        // Basic validation
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _signUpResult.value = "Fields cannot be empty"
            return
        }

        // For now, simulate success
        // Here, you could add validation or save to local database
        _signUpResult.value = "Registration successful! Please proceed to Sign In."
    }
}
