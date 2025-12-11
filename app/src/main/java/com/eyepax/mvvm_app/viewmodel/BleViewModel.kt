package com.eyepax.mvvm_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eyepax.mvvm_app.database.AppDatabase
import com.eyepax.mvvm_app.model.BleDevice
import com.eyepax.mvvm_app.repository.BleRepository
import com.eyepax.mvvm_app.util.BleScanner
import kotlinx.coroutines.launch

class BleViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "BleViewModel"
    private val repository: BleRepository
    private val bleScanner: BleScanner

    val devices: LiveData<List<BleDevice>>

    private val _isScanning = MutableLiveData<Boolean>()
    val isScanning: LiveData<Boolean> = _isScanning

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        val dao = AppDatabase.getDatabase(application).bleDeviceDao()
        repository = BleRepository(dao)
        devices = repository.allDevices
        bleScanner = BleScanner(application)

        _isScanning.value = false
    }

    fun startScan() {
        if (_isScanning.value == true) {
            Log.w(TAG, "Already scanning")
            return
        }

        if (!bleScanner.isBluetoothEnabled()) {
            _errorMessage.value = "Bluetooth is disabled. Please enable Bluetooth."
            return
        }

        _isScanning.value = true
        Log.d(TAG, "Starting BLE scan...")

        bleScanner.startScan { scannedDevices ->
            viewModelScope.launch {
                repository.saveDevices(scannedDevices)
                Log.d(TAG, "Saved ${scannedDevices.size} BLE devices")
            }
        }
    }

    fun stopScan() {
        bleScanner.stopScan()
        _isScanning.value = false
        Log.d(TAG, "BLE scan stopped")
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}
