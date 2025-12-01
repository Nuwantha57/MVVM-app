package com.eyepax.mvvm_app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.util.Resource
import com.eyepax.mvvm_app.util.SessionManager
import com.eyepax.mvvm_app.viewmodel.SignInViewModel

class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by viewModels()

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvGoToSignUp: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        initViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvStatus = findViewById(R.id.tvStatus)
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupObservers() {
        // Observe login result
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    tvStatus.text = result.data?.message ?: "Login successful"
                }

                is Resource.Error -> {
                    tvStatus.text = result.message
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    tvStatus.text = "Logging in..."
                }
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                btnLogin.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
            }
        }


        viewModel.completeUserData.observe(this) { userData ->

            // SAVE SESSION BEFORE NAVIGATING
            val sessionManager = SessionManager(this)
            sessionManager.saveUserSession(
                accessToken = userData.accessToken,
                refreshToken = userData.refreshToken,
                idToken = userData.idToken,
                userId = userData.userInfo.userId,
                email = userData.userInfo.email,
                name = userData.userInfo.name
            )

            // Now navigate to Flutter
            val intent = FlutterHomeActivity.createIntent(
                context = this,
                userId = userData.userInfo.userId,
                name = userData.userInfo.name,
                email = userData.userInfo.email,
                accessToken = userData.accessToken,
                idToken = userData.idToken,
                refreshToken = userData.refreshToken
            )
            startActivity(intent)
            finish()
        }

    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            viewModel.login(username, password)
        }

        tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
