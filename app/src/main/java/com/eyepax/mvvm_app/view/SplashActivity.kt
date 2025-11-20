package com.eyepax.mvvm_app.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        splashViewModel.isFinished.observe(this) { finished ->
            if (finished) {
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
        splashViewModel.startTimer()
    }
}
