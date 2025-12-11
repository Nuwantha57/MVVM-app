package com.eyepax.mvvm_app.util

import android.content.Context
import android.util.Log
import com.eyepax.mvvm_app.model.WiFiNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MockWiFiScanner(private val context: Context) {

    private val TAG = "MockWiFiScanner"

    fun startScan(onScanComplete: (List<WiFiNetwork>) -> Unit) {
        Log.d(TAG, "Starting MOCK Wi-Fi scan...")

        CoroutineScope(Dispatchers.IO).launch {
            delay(2000) // Simulate scan delay

            val mockNetworks = listOf(
                WiFiNetwork(
                    bssid = generateMockBssid(),
                    ssid = "Home_WiFi_5G",
                    signalLevel = 4,
                    capabilities = "[WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS]",
                    frequency = 5180,
                    lastSeen = System.currentTimeMillis(),
                    isSaved = false
                ),
                WiFiNetwork(
                    bssid = generateMockBssid(),
                    ssid = "Office_Network",
                    signalLevel = 3,
                    capabilities = "[WPA3-SAE-CCMP][ESS]",
                    frequency = 2437,
                    lastSeen = System.currentTimeMillis(),
                    isSaved = false
                ),
                WiFiNetwork(
                    bssid = generateMockBssid(),
                    ssid = "TP-Link_Guest",
                    signalLevel = 2,
                    capabilities = "[WPA2-PSK-CCMP][ESS]",
                    frequency = 2462,
                    lastSeen = System.currentTimeMillis(),
                    isSaved = false
                ),
                WiFiNetwork(
                    bssid = generateMockBssid(),
                    ssid = "Public_WiFi",
                    signalLevel = 1,
                    capabilities = "[ESS]",
                    frequency = 2412,
                    lastSeen = System.currentTimeMillis(),
                    isSaved = false
                ),
                WiFiNetwork(
                    bssid = generateMockBssid(),
                    ssid = "Starbucks_WiFi",
                    signalLevel = 5,
                    capabilities = "[WPA2-PSK-CCMP][ESS]",
                    frequency = 5240,
                    lastSeen = System.currentTimeMillis(),
                    isSaved = false
                )
            )

            Log.d(TAG, "Found ${mockNetworks.size} mock Wi-Fi networks")
            onScanComplete(mockNetworks)
        }
    }

    fun stopScan() {
        Log.d(TAG, "Mock Wi-Fi scan stopped")
    }

    fun isWiFiEnabled(): Boolean {
        return true
    }

    private fun generateMockBssid(): String {
        return (1..6).joinToString(":") {
            "%02x".format(Random.nextInt(0, 256))
        }
    }
}
