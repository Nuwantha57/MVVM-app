package com.eyepax.mvvm_app.view

import android.content.Intent
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eyepax.mvvm_app.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // Permission launcher for Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            getFCMToken()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Request notification permission
        requestNotificationPermission()

        // Setup buttons
        setupCrashlyticsTestButton()
        setupFCMButton()

        findViewById<Button>(R.id.btnTestSSL)?.setOnClickListener {
            startActivity(Intent(this, SSLPinningTestActivity::class.java))
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "Notification permission granted")
                    getFCMToken()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Permission not needed for Android < 13
            getFCMToken()
        }
    }

    private fun setupCrashlyticsTestButton() {
        findViewById<Button>(R.id.btnTestCrash)?.setOnClickListener {
            Toast.makeText(this, "Triggering test crash...", Toast.LENGTH_SHORT).show()

            FirebaseCrashlytics.getInstance().apply {
                log("User clicked Test Crash button")
                setCustomKey("test_mode", true)
            }

            throw RuntimeException("Test Crash: Firebase Crashlytics Demo")
        }
    }

    private fun setupFCMButton() {
        findViewById<Button>(R.id.btnGetFCMToken)?.setOnClickListener {
            getFCMToken()
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
                Toast.makeText(this, "Failed to get FCM token", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }

            // Get FCM token
            val token = task.result
            Log.d(TAG, "FCM Token: $token")

            // Copy token to clipboard
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("FCM Token", token)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "FCM Token copied to clipboard!", Toast.LENGTH_LONG).show()
        }
    }
}
