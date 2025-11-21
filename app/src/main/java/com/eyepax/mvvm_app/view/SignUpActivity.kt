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
import com.eyepax.mvvm_app.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    private lateinit var etUsername: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvGoToSignIn: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvStatus = findViewById(R.id.tvStatus)
        tvGoToSignIn = findViewById(R.id.tvGoToSignIn)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupObservers() {
        // Observe sign up result
        viewModel.signUpResult.observe(this) { (success, message) ->
            tvStatus.text = message
            tvStatus.setTextColor(if (success) 0xFF00FF00.toInt() else 0xFFFF0000.toInt())

            if (success) {
                // Navigate to Sign In after successful registration
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }, 2000)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                btnSignUp.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                btnSignUp.isEnabled = true
            }
        }
    }

    private fun setupClickListeners() {
        // Sign Up button click
        btnSignUp.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            viewModel.signUp(
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                email = email,
                name = name,
                phoneNumber = phoneNumber
            )
        }

        // Navigate to Sign In
        tvGoToSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
