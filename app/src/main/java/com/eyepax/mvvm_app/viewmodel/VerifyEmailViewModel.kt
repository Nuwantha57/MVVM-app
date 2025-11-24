package com.eyepax.mvvm_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eyepax.mvvm_app.repository.AuthRepository
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.launch

class VerifyEmailViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val TAG = "VerifyEmailViewModel"

    private val _verifyResult = MutableLiveData<Pair<Boolean, String>>()
    val verifyResult: LiveData<Pair<Boolean, String>> get() = _verifyResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun verifyEmail(username: String, code: String) {
        if (username.isBlank() || code.isBlank()) {
            _verifyResult.value = Pair(false, "Username and code are required")
            return
        }

        if (code.length != 6) {
            _verifyResult.value = Pair(false, "Code must be 6 digits")
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.verifyEmail(username, code)

                when (result) {
                    is Resource.Success -> {
                        val response = result.data
                        if (response != null && response.success) {
                            _verifyResult.value = Pair(true, response.message)
                        } else {
                            _verifyResult.value = Pair(false, response?.message ?: "Verification failed")
                        }
                    }
                    is Resource.Error -> {
                        _verifyResult.value = Pair(false, result.message ?: "Verification failed")
                    }
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during verification", e)
                _verifyResult.value = Pair(false, "An error occurred: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resendCode(username: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.resendCode(username)

                when (result) {
                    is Resource.Success -> {
                        _verifyResult.value = Pair(true, "Code resent successfully")
                    }
                    is Resource.Error -> {
                        _verifyResult.value = Pair(false, result.message ?: "Failed to resend code")
                    }
                    is Resource.Loading -> {}
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
