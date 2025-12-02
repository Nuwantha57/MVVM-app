package com.eyepax.mvvm_app.repository

import androidx.lifecycle.LiveData
import com.eyepax.mvvm_app.database.WiFiNetworkDao
import com.eyepax.mvvm_app.model.WiFiNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WiFiRepository(private val wifiNetworkDao: WiFiNetworkDao) {

    val allNetworks: LiveData<List<WiFiNetwork>> = wifiNetworkDao.getAllNetworks()

    suspend fun saveNetworks(networks: List<WiFiNetwork>) {
        withContext(Dispatchers.IO) {
            wifiNetworkDao.insertNetworks(networks)
        }
    }

    suspend fun clearNetworks() {
        withContext(Dispatchers.IO) {
            wifiNetworkDao.deleteAllNetworks()
        }
    }
}
