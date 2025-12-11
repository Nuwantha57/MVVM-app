package com.eyepax.mvvm_app.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {
    private val _isFinished = MutableLiveData(false)
    val isFinished: LiveData<Boolean> get() = _isFinished

    fun startTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            _isFinished.value = true
        }, 4000) // 4 seconds splash
    }
}
