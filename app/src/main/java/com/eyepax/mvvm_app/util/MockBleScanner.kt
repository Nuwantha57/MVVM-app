package com.eyepax.mvvm_app.util

import android.content.Context
import android.util.Log
import com.eyepax.mvvm_app.model.BleDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MockBleScanner(private val context: Context) {

    private val TAG = "MockBleScanner"
    private var isScanning = false

    fun startScan(onDeviceFound: (List<BleDevice>) -> Unit) {
        if (isScanning) return

        isScanning = true
        Log.d(TAG, "Starting MOCK BLE scan...")

        // Simulate scanning with fake devices
        CoroutineScope(Dispatchers.IO).launch {
            val devices = mutableListOf<BleDevice>()

            // Simulate finding devices one by one
            repeat(5) { index ->
                delay(1000) // 1 second delay between devices

                val mockDevice = BleDevice(
                    address = generateMockMacAddress(),
                    name = getMockDeviceName(index),
                    rssi = Random.nextInt(-90, -40),
                    lastSeen = System.currentTimeMillis(),
                    isFavorite = false
                )

                devices.add(mockDevice)
                onDeviceFound(devices.toList())
                Log.d(TAG, "Found mock device: ${mockDevice.name}")
            }

            isScanning = false
        }
    }

    fun stopScan() {
        isScanning = false
        Log.d(TAG, "Mock BLE scan stopped")
    }

    fun isBluetoothEnabled(): Boolean {
        return true // Always return true for mock
    }

    private fun generateMockMacAddress(): String {
        return (1..6).joinToString(":") {
            "%02X".format(Random.nextInt(0, 256))
        }
    }

    private fun getMockDeviceName(index: Int): String {
        val names = listOf(
            "Samsung Galaxy Watch",
            "Apple AirPods Pro",
            "Fitbit Charge 5",
            "Mi Band 7",
            "Sony WH-1000XM5"
        )
        return names[index % names.size]
    }
}
