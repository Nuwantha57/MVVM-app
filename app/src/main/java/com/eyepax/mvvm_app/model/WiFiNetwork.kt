package com.eyepax.mvvm_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi_networks")
data class WiFiNetwork(
    @PrimaryKey
    val bssid: String,           // MAC address (unique)
    val ssid: String,            // Network name
    val signalLevel: Int,        // Signal strength
    val capabilities: String,    // Security type (WPA2, WPA3, etc.)
    val frequency: Int,          // 2.4GHz or 5GHz
    val lastSeen: Long,          // Timestamp
    val isSaved: Boolean = false // User saved this network
)
