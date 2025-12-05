package com.eyepax.mvvm_app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import com.eyepax.mvvm_app.util.BleManager
import com.eyepax.mvvm_app.util.WiFiManager
import io.flutter.embedding.android.FlutterFragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FlutterHomeActivity : FlutterFragmentActivity() {

    private val METHOD_CHANNEL = "com.eyepax.mvvm_app/user_data"
    private val BLE_EVENT_CHANNEL = "com.eyepax.mvvm_app/ble_stream"
    private val WIFI_EVENT_CHANNEL = "com.eyepax.mvvm_app/wifi_stream"

    private var userData: HashMap<String, Any>? = null
    private lateinit var bleManager: BleManager
    private lateinit var wifiManager: WiFiManager
    private var bleEventSink: EventChannel.EventSink? = null
    private var wifiEventSink: EventChannel.EventSink? = null

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
        bleManager = BleManager(this)
        wifiManager = WiFiManager(this)
        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Method Channel for commands
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            METHOD_CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "getUserData" -> {
                    if (userData != null) {
                        result.success(userData)
                    } else {
                        result.error("UNAVAILABLE", "User data not available", null)
                    }
                }

                // BLE Methods
                "startBleScan" -> {
                    Log.d("FlutterHomeActivity", "Starting BLE scan...")
                    bleManager.startScan { devices ->
                        bleEventSink?.success(devices)
                    }
                    result.success(true)
                }

                "stopBleScan" -> {
                    Log.d("FlutterHomeActivity", "Stopping BLE scan...")
                    bleManager.stopScan()
                    result.success(true)
                }

                "getSavedBleDevices" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val devices = bleManager.getSavedDevices()
                        result.success(devices)
                    }
                }

                "isBluetoothEnabled" -> {
                    val isEnabled = bleManager.isBluetoothEnabled()
                    result.success(isEnabled)
                }

                // Wi-Fi Methods - ADD THESE
                "startWiFiScan" -> {
                    Log.d("FlutterHomeActivity", "Starting Wi-Fi scan...")
                    wifiManager.startScan { networks ->
                        wifiEventSink?.success(networks)
                    }
                    result.success(true)
                }

                "stopWiFiScan" -> {
                    Log.d("FlutterHomeActivity", "Stopping Wi-Fi scan...")
                    wifiManager.stopScan()
                    result.success(true)
                }

                "getSavedWiFiNetworks" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val networks = wifiManager.getSavedNetworks()
                        result.success(networks)
                    }
                }

                "isWiFiEnabled" -> {
                    val isEnabled = wifiManager.isWiFiEnabled()
                    result.success(isEnabled)
                }

                else -> result.notImplemented()
            }
        }

        // BLE Event Channel
        EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            BLE_EVENT_CHANNEL
        ).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                bleEventSink = events
                Log.d("FlutterHomeActivity", "BLE Event stream started")
            }

            override fun onCancel(arguments: Any?) {
                bleEventSink = null
                bleManager.stopScan()
                Log.d("FlutterHomeActivity", "BLE Event stream cancelled")
            }
        })

        // Wi-Fi Event Channel - ADD THIS
        EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            WIFI_EVENT_CHANNEL
        ).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                wifiEventSink = events
                Log.d("FlutterHomeActivity", "Wi-Fi Event stream started")
            }

            override fun onCancel(arguments: Any?) {
                wifiEventSink = null
                wifiManager.stopScan()
                Log.d("FlutterHomeActivity", "Wi-Fi Event stream cancelled")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        bleManager.stopScan()
        wifiManager.stopScan()
    }
}
