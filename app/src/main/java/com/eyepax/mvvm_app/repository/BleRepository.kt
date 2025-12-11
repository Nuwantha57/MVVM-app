package com.eyepax.mvvm_app.repository

import androidx.lifecycle.LiveData
import com.eyepax.mvvm_app.database.BleDeviceDao
import com.eyepax.mvvm_app.model.BleDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BleRepository(private val bleDeviceDao: BleDeviceDao) {

    val allDevices: LiveData<List<BleDevice>> = bleDeviceDao.getAllDevices()

    suspend fun saveDevices(devices: List<BleDevice>) {
        withContext(Dispatchers.IO) {
            bleDeviceDao.insertDevices(devices)
        }
    }

    suspend fun clearDevices() {
        withContext(Dispatchers.IO) {
            bleDeviceDao.deleteAllDevices()
        }
    }
}
