package com.eyepax.mvvm_app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eyepax.mvvm_app.model.WiFiNetwork

@Dao
interface WiFiNetworkDao {

    @Query("SELECT * FROM wifi_networks ORDER BY signalLevel DESC")
    fun getAllNetworks(): LiveData<List<WiFiNetwork>>

    // ADD THIS METHOD
    @Query("SELECT * FROM wifi_networks ORDER BY signalLevel DESC")
    suspend fun getAllNetworksSync(): List<WiFiNetwork>

    @Query("SELECT * FROM wifi_networks WHERE isSaved = 1")
    fun getSavedNetworks(): LiveData<List<WiFiNetwork>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetwork(network: WiFiNetwork)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetworks(networks: List<WiFiNetwork>)

    @Query("DELETE FROM wifi_networks")
    suspend fun deleteAllNetworks()

    @Query("UPDATE wifi_networks SET isSaved = :saved WHERE bssid = :bssid")
    suspend fun updateSaved(bssid: String, saved: Boolean)
}
