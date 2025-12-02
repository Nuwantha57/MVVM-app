package com.eyepax.mvvm_app.util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.eyepax.mvvm_app.model.BleDevice

class BleScanner(private val context: Context) {

    private val TAG = "BleScanner"
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bleScanner: BluetoothLeScanner? = null
    private var isScanning = false

    private val scannedDevices = mutableListOf<BleDevice>()
    private var onDeviceFoundCallback: ((List<BleDevice>) -> Unit)? = null

    init {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bleScanner = bluetoothAdapter?.bluetoothLeScanner
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val device = BleDevice(
                        address = it.device.address,
                        name = it.device.name ?: "Unknown Device",
                        rssi = it.rssi,
                        lastSeen = System.currentTimeMillis(),
                        isFavorite = false
                    )

                    // Update or add device
                    val existingIndex = scannedDevices.indexOfFirst { d -> d.address == device.address }
                    if (existingIndex != -1) {
                        scannedDevices[existingIndex] = device
                    } else {
                        scannedDevices.add(device)
                    }

                    onDeviceFoundCallback?.invoke(scannedDevices.toList())
                    Log.d(TAG, "Found device: ${device.name} (${device.address}) RSSI: ${device.rssi}")
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE Scan failed with error: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(onDeviceFound: (List<BleDevice>) -> Unit) {
        if (isScanning) {
            Log.w(TAG, "Already scanning")
            return
        }

        if (!hasPermissions()) {
            Log.e(TAG, "Missing BLE permissions")
            return
        }

        scannedDevices.clear()
        onDeviceFoundCallback = onDeviceFound

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            bleScanner?.startScan(scanCallback)
            isScanning = true
            Log.d(TAG, "BLE scan started")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!isScanning) return

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            bleScanner?.stopScan(scanCallback)
            isScanning = false
            Log.d(TAG, "BLE scan stopped")
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }
}
