package com.eyepax.mvvm_app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.util.SessionManager


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sessionManager = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({

            // CHECK IF USER IS ALREADY LOGGED IN
            if (sessionManager.isLoggedIn()) {
                // User has active session, go to home
                val intent = FlutterHomeActivity.createIntent(
                    context = this,
                    userId = sessionManager.getUserId() ?: "",
                    name = sessionManager.getUserName() ?: "",
                    email = sessionManager.getUserEmail() ?: "",
                    accessToken = sessionManager.getAccessToken() ?: "",
                    idToken = sessionManager.getIdToken(),
                    refreshToken = sessionManager.getRefreshToken()
                )
                startActivity(intent)
            } else {
                // No session, go to sign in
                startActivity(Intent(this, SignInActivity::class.java))
            }
            finish()
        }, 2000)
    }
}

