package com.eyepax.mvvm_app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class FlutterHomeActivity : FlutterActivity() {

    private val CHANNEL = "com.eyepax.mvvm_app/user_data"
    private var userData: HashMap<String, Any>? = null

    companion object {
        private const val EXTRA_USER_DATA = "user_data"

        fun createIntent(
            context: Context,
            userId: String,
            name: String,
            email: String,
            accessToken: String,
            idToken: String? = null,
            refreshToken: String? = null
        ): Intent {
            val userDataMap = hashMapOf<String, Any>(
                "userId" to userId,
                "name" to name,
                "email" to email,
                "accessToken" to accessToken
            )

            idToken?.let { userDataMap["idToken"] = it }
            refreshToken?.let { userDataMap["refreshToken"] = it }

            return Intent(context, FlutterHomeActivity::class.java).apply {
                putExtra(EXTRA_USER_DATA, userDataMap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("UNCHECKED_CAST")
        userData = intent.getSerializableExtra(EXTRA_USER_DATA) as? HashMap<String, Any>
        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "getUserData" -> {
                    if (userData != null) {
                        result.success(userData)
                    } else {
                        result.error(
                            "UNAVAILABLE",
                            "User data not available",
                            null
                        )
                    }
                }
                else -> result.notImplemented()
            }
        }
    }
}
