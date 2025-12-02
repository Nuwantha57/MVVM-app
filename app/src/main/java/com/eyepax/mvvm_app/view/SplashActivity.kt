package com.eyepax.mvvm_app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.viewmodel.SplashViewModel

// show countries data
//class SplashActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
//
//        // Navigate to CountriesActivity after 2 seconds
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, CountriesActivity::class.java))
//            finish()
//        }, 2000)
//    }
//}

// directly go home without authentication

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            // Go directly to Flutter Home with mock user data
            val intent = FlutterHomeActivity.createIntent(
                context = this,
                userId = "user123",
                name = "Test User",
                email = "test@example.com",
                accessToken = "mock_token",
                idToken = null,
                refreshToken = null
            )
            startActivity(intent)
            finish()

        }, 2000) // 2 seconds delay
    }
}



//class SplashActivity : AppCompatActivity() {
//    private val splashViewModel: SplashViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_splash)
//
//        splashViewModel.isFinished.observe(this) { finished ->
//            if (finished) {
//                startActivity(Intent(this, SignInActivity::class.java))
//                finish()
//            }
//        }
//        splashViewModel.startTimer()
//    }
//}
