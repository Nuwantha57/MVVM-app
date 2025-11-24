package com.eyepax.mvvm_app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.viewmodel.VerifyEmailViewModel
import com.google.android.material.textfield.TextInputEditText

class VerifyEmailActivity : AppCompatActivity() {

    private val viewModel: VerifyEmailViewModel by viewModels()

    private lateinit var tvUsername: TextView
    private lateinit var etCode: TextInputEditText
    private lateinit var btnVerify: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvResendCode: TextView
    private lateinit var progressBar: ProgressBar

    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verify_email)

        // Get username from intent
        username = intent.getStringExtra("username") ?: ""

        if (username.isEmpty()) {
            finish()
            return
        }

        initViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        tvUsername = findViewById(R.id.tvUsername)
        etCode = findViewById(R.id.etCode)
        btnVerify = findViewById(R.id.btnVerify)
        tvStatus = findViewById(R.id.tvStatus)
        tvResendCode = findViewById(R.id.tvResendCode)
        progressBar = findViewById(R.id.progressBar)

        tvUsername.text = username
    }

    private fun setupObservers() {
        viewModel.verifyResult.observe(this) { (success, message) ->
            tvStatus.text = message
            tvStatus.visibility = View.VISIBLE

            if (success) {
                tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))

                // Navigate to Sign In after 2 seconds
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                }, 2000)
            } else {
                tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnVerify.isEnabled = !isLoading
            tvResendCode.isEnabled = !isLoading
        }
    }

    private fun setupClickListeners() {
        btnVerify.setOnClickListener {
            val code = etCode.text.toString().trim()
            viewModel.verifyEmail(username, code)
        }

        tvResendCode.setOnClickListener {
            viewModel.resendCode(username)
        }
    }
}
