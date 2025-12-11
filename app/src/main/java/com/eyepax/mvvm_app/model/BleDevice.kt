package com.eyepax.mvvm_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ble_devices")
data class BleDevice(
    @PrimaryKey
    val address: String,        // MAC address (unique)
    val name: String?,           // Device name
    val rssi: Int,               // Signal strength
    val lastSeen: Long,          // Timestamp
    val isFavorite: Boolean = false
)
