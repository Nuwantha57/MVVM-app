package com.eyepax.mvvm_app.util

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.eyepax.mvvm_app.model.WiFiNetwork

class WiFiScanner(private val context: Context) {

    private val TAG = "WiFiScanner"
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var onScanCompleteCallback: ((List<WiFiNetwork>) -> Unit)? = null

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    fun startScan(onScanComplete: (List<WiFiNetwork>) -> Unit) {
        if (!hasPermissions()) {
            Log.e(TAG, "Missing Wi-Fi permissions")
            return
        }

        onScanCompleteCallback = onScanComplete

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            Log.e(TAG, "Wi-Fi scan failed to start")
            scanFailure()
        } else {
            Log.d(TAG, "Wi-Fi scan started")
        }
    }

    private fun scanSuccess() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val results = wifiManager.scanResults
        val networks = results.map { scanResult ->
            WiFiNetwork(
                bssid = scanResult.BSSID,
                ssid = scanResult.SSID,
                signalLevel = WifiManager.calculateSignalLevel(scanResult.level, 5),
                capabilities = scanResult.capabilities,
                frequency = scanResult.frequency,
                lastSeen = System.currentTimeMillis(),
                isSaved = false
            )
        }

        Log.d(TAG, "Found ${networks.size} Wi-Fi networks")
        onScanCompleteCallback?.invoke(networks)
        stopScan()
    }

    private fun scanFailure() {
        Log.e(TAG, "Wi-Fi scan failure")
        onScanCompleteCallback?.invoke(emptyList())
        stopScan()
    }

    fun stopScan() {
        try {
            context.unregisterReceiver(wifiScanReceiver)
        } catch (e: Exception) {
            // Receiver not registered
        }
    }

    fun isWiFiEnabled(): Boolean {
        return wifiManager.isWifiEnabled
    }

    private fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
