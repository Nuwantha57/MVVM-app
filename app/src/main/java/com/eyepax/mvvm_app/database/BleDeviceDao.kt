package com.eyepax.mvvm_app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eyepax.mvvm_app.model.BleDevice

@Dao
interface BleDeviceDao {

    @Query("SELECT * FROM ble_devices ORDER BY rssi DESC")
    fun getAllDevices(): LiveData<List<BleDevice>>

    // ADD THIS METHOD (for synchronous access from Platform Channel)
    @Query("SELECT * FROM ble_devices ORDER BY rssi DESC")
    suspend fun getAllDevicesSync(): List<BleDevice>

    @Query("SELECT * FROM ble_devices WHERE isFavorite = 1")
    fun getFavoriteDevices(): LiveData<List<BleDevice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: BleDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<BleDevice>)

    @Query("DELETE FROM ble_devices")
    suspend fun deleteAllDevices()

    @Query("UPDATE ble_devices SET isFavorite = :favorite WHERE address = :address")
    suspend fun updateFavorite(address: String, favorite: Boolean)
}
