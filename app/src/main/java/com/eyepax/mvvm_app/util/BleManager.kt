//package com.eyepax.mvvm_app.util
//
//import android.content.Context
//import android.util.Log
//import com.eyepax.mvvm_app.database.AppDatabase
//import com.eyepax.mvvm_app.model.BleDevice
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class BleManager(private val context: Context) {
//
//    private val TAG = "BleManager"
//    private val bleScanner = BleScanner(context)
//    private val bleDeviceDao = AppDatabase.getDatabase(context).bleDeviceDao()
//    private var isScanning = false
//
//    // Callback to send devices to Flutter
//    private var onDevicesUpdated: ((List<Map<String, Any>>) -> Unit)? = null
//
//    fun startScan(onDevicesCallback: (List<Map<String, Any>>) -> Unit) {
//        if (isScanning) {
//            Log.w(TAG, "Already scanning")
//            return
//        }
//
//        if (!bleScanner.isBluetoothEnabled()) {
//            onDevicesCallback(emptyList())
//            return
//        }
//
//        onDevicesUpdated = onDevicesCallback
//        isScanning = true
//        Log.d(TAG, "Starting BLE scan...")
//
//        bleScanner.startScan { devices ->
//            // Save to Room database
//            CoroutineScope(Dispatchers.IO).launch {
//                bleDeviceDao.insertDevices(devices)
//                Log.d(TAG, "Saved ${devices.size} devices to Room")
//            }
//
//            // Convert to Map for Flutter
//            val deviceMaps = devices.map { device ->
//                mapOf(
//                    "address" to device.address,
//                    "name" to (device.name ?: "Unknown Device"),
//                    "rssi" to device.rssi,
//                    "lastSeen" to device.lastSeen
//                )
//            }
//
//            // Send to Flutter
//            onDevicesUpdated?.invoke(deviceMaps)
//        }
//    }
//
//    fun stopScan() {
//        if (isScanning) {
//            bleScanner.stopScan()
//            isScanning = false
//            Log.d(TAG, "BLE scan stopped")
//        }
//    }
//
//    suspend fun getSavedDevices(): List<Map<String, Any>> {
//        return try {
//            val devices = bleDeviceDao.getAllDevicesSync()
//            devices.map { device ->
//                mapOf(
//                    "address" to device.address,
//                    "name" to (device.name ?: "Unknown Device"),
//                    "rssi" to device.rssi,
//                    "lastSeen" to device.lastSeen
//                )
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error getting saved devices", e)
//            emptyList()
//        }
//    }
//
//    fun isBluetoothEnabled(): Boolean {
//        return bleScanner.isBluetoothEnabled()
//    }
//}

//Update BleManager to Use Mock Data in Emulator

package com.eyepax.mvvm_app.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.eyepax.mvvm_app.database.AppDatabase
import com.eyepax.mvvm_app.model.BleDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BleManager(private val context: Context) {

    private val TAG = "BleManager"
    private val bleScanner = BleScanner(context)
    private val mockBleScanner = MockBleScanner(context) // ADD THIS
    private val bleDeviceDao = AppDatabase.getDatabase(context).bleDeviceDao()
    private var isScanning = false

    private var onDevicesUpdated: ((List<Map<String, Any>>) -> Unit)? = null

    fun startScan(onDevicesCallback: (List<Map<String, Any>>) -> Unit) {
        if (isScanning) {
            Log.w(TAG, "Already scanning")
            return
        }

        onDevicesUpdated = onDevicesCallback
        isScanning = true

        // Check if running on emulator
        if (isEmulator()) {
            Log.d(TAG, "Running on emulator, using MOCK data")
            startMockScan()
        } else {
            Log.d(TAG, "Running on real device, using REAL BLE")
            startRealScan()
        }
    }

    private fun startRealScan() {
        if (!bleScanner.isBluetoothEnabled()) {
            onDevicesUpdated?.invoke(emptyList())
            isScanning = false
            return
        }

        Log.d(TAG, "Starting REAL BLE scan...")
        bleScanner.startScan { devices ->
            saveAndSendDevices(devices)
        }
    }

    private fun startMockScan() {
        Log.d(TAG, "Starting MOCK BLE scan...")
        mockBleScanner.startScan { devices ->
            saveAndSendDevices(devices)
        }
    }

    private fun saveAndSendDevices(devices: List<BleDevice>) {
        CoroutineScope(Dispatchers.IO).launch {
            bleDeviceDao.insertDevices(devices)
            Log.d(TAG, "Saved ${devices.size} devices to Room")
        }

        val deviceMaps = devices.map { device ->
            mapOf(
                "address" to device.address,
                "name" to (device.name ?: "Unknown Device"),
                "rssi" to device.rssi,
                "lastSeen" to device.lastSeen
            )
        }

        onDevicesUpdated?.invoke(deviceMaps)
    }

    fun stopScan() {
        if (isScanning) {
            if (isEmulator()) {
                mockBleScanner.stopScan()
            } else {
                bleScanner.stopScan()
            }
            isScanning = false
            Log.d(TAG, "BLE scan stopped")
        }
    }

    suspend fun getSavedDevices(): List<Map<String, Any>> {
        return try {
            val devices = bleDeviceDao.getAllDevicesSync()
            devices.map { device ->
                mapOf(
                    "address" to device.address,
                    "name" to (device.name ?: "Unknown Device"),
                    "rssi" to device.rssi,
                    "lastSeen" to device.lastSeen
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting saved devices", e)
            emptyList()
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return if (isEmulator()) {
            true // Always return true for emulator
        } else {
            bleScanner.isBluetoothEnabled()
        }
    }

    // Detect if running on emulator
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
