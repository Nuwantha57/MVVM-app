package com.eyepax.mvvm_app.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.eyepax.mvvm_app.database.CountryDao
import com.eyepax.mvvm_app.model.Country
import com.eyepax.mvvm_app.network.CountryRetrofitClient
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CountryRepository(private val countryDao: CountryDao) {

    private val TAG = "CountryRepository"
    private val apiService = CountryRetrofitClient.apiService

    // LiveData from Room - auto-updates UI
    val allCountries: LiveData<List<Country>> = countryDao.getAllCountries()

    // Fetch from API and save to Room
    suspend fun refreshCountries(): Resource<List<Country>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching countries from REST API...")

                val response = apiService.getAllCountries()

                if (response.isSuccessful && response.body() != null) {
                    val countries = response.body()!!
                    Log.d(TAG, "Fetched ${countries.size} countries")

                    // Clear old data and insert new
                    countryDao.deleteAllCountries()
                    countryDao.insertCountries(countries)

                    Log.d(TAG, "Countries saved to Room database")
                    Resource.Success(countries)
                } else {
                    Log.e(TAG, "API error: ${response.message()}")
                    Resource.Error("Failed to fetch countries: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching countries", e)
                Resource.Error(e.message ?: "Network error occurred")
            }
        }
    }

    // Check if local database has data
    suspend fun hasLocalData(): Boolean {
        return withContext(Dispatchers.IO) {
            countryDao.getCountryCount() > 0
        }
    }
}
