package com.eyepax.mvvm_app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eyepax.mvvm_app.R
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

        // Initialize views
        initViews()

        // Set up observers
        setupObservers()

        // Set up click listeners
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
        viewModel.loginResult.observe(this) { (success, message) ->
            tvStatus.text = message

            if (success) {
                // Navigate to Home on success
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
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

        // Observe access token (optional - for debugging)
        viewModel.accessToken.observe(this) { token ->
            token?.let {
                // Token received - you can save it to SharedPreferences here
                android.util.Log.d("SignInActivity", "Access Token: $it")
            }
        }
    }

    private fun setupClickListeners() {
        // Login button click
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            viewModel.login(username, password)
        }

        // Navigate to Sign Up
        tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
