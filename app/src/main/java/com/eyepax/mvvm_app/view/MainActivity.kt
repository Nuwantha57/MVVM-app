package com.eyepax.mvvm_app.view

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eyepax.mvvm_app.R
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup Crashlytics test button
        setupCrashlyticsTestButton()
    }

    private fun setupCrashlyticsTestButton() {
        findViewById<Button>(R.id.btnTestCrash)?.setOnClickListener {
            Toast.makeText(this, "Triggering test crash...", Toast.LENGTH_SHORT).show()

            // Log details before crash
            FirebaseCrashlytics.getInstance().apply {
                log("User clicked Test Crash button")
                setCustomKey("test_mode", true)
                setCustomKey("screen", "MainActivity")
                setCustomKey("user_action", "test_crash_button")
            }

            // Force crash for Crashlytics demo
            throw RuntimeException("Test Crash: Firebase Crashlytics Demo")
        }
    }
}
