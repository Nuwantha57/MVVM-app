package com.eyepax.mvvm_app.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.eyepax.mvvm_app.database.AppDatabase
import com.eyepax.mvvm_app.model.WiFiNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WiFiManager(private val context: Context) {

    private val TAG = "WiFiManager"
    private val wifiScanner = WiFiScanner(context)
    private val mockWiFiScanner = MockWiFiScanner(context)
    private val wifiNetworkDao = AppDatabase.getDatabase(context).wifiNetworkDao()
    private var isScanning = false

    private var onNetworksUpdated: ((List<Map<String, Any>>) -> Unit)? = null

    fun startScan(onNetworksCallback: (List<Map<String, Any>>) -> Unit) {
        if (isScanning) {
            Log.w(TAG, "Already scanning")
            return
        }

        onNetworksUpdated = onNetworksCallback
        isScanning = true

        if (isEmulator()) {
            Log.d(TAG, "Running on emulator, using MOCK Wi-Fi data")
            startMockScan()
        } else {
            Log.d(TAG, "Running on real device, using REAL Wi-Fi")
            startRealScan()
        }
    }

    private fun startRealScan() {
        if (!wifiScanner.isWiFiEnabled()) {
            onNetworksUpdated?.invoke(emptyList())
            isScanning = false
            return
        }

        Log.d(TAG, "Starting REAL Wi-Fi scan...")
        wifiScanner.startScan { networks ->
            saveAndSendNetworks(networks)
            isScanning = false
        }
    }

    private fun startMockScan() {
        Log.d(TAG, "Starting MOCK Wi-Fi scan...")
        mockWiFiScanner.startScan { networks ->
            saveAndSendNetworks(networks)
            isScanning = false
        }
    }

    private fun saveAndSendNetworks(networks: List<WiFiNetwork>) {
        CoroutineScope(Dispatchers.IO).launch {
            wifiNetworkDao.insertNetworks(networks)
            Log.d(TAG, "Saved ${networks.size} networks to Room")
        }

        val networkMaps = networks.map { network ->
            mapOf(
                "bssid" to network.bssid,
                "ssid" to network.ssid,
                "signalLevel" to network.signalLevel,
                "capabilities" to network.capabilities,
                "frequency" to network.frequency,
                "lastSeen" to network.lastSeen,
                "isSaved" to network.isSaved
            )
        }

        onNetworksUpdated?.invoke(networkMaps)
    }

    fun stopScan() {
        if (isScanning) {
            if (isEmulator()) {
                mockWiFiScanner.stopScan()
            } else {
                wifiScanner.stopScan()
            }
            isScanning = false
            Log.d(TAG, "Wi-Fi scan stopped")
        }
    }

    suspend fun getSavedNetworks(): List<Map<String, Any>> {
        return try {
            val networks = wifiNetworkDao.getAllNetworksSync()
            networks.map { network ->
                mapOf(
                    "bssid" to network.bssid,
                    "ssid" to network.ssid,
                    "signalLevel" to network.signalLevel,
                    "capabilities" to network.capabilities,
                    "frequency" to network.frequency,
                    "lastSeen" to network.lastSeen,
                    "isSaved" to network.isSaved
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting saved networks", e)
            emptyList()
        }
    }

    fun isWiFiEnabled(): Boolean {
        return if (isEmulator()) {
            true
        } else {
            wifiScanner.isWiFiEnabled()
        }
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }
}
