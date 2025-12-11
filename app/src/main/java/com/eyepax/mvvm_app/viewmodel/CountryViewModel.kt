package com.eyepax.mvvm_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eyepax.mvvm_app.database.AppDatabase
import com.eyepax.mvvm_app.model.Country
import com.eyepax.mvvm_app.repository.CountryRepository
import com.eyepax.mvvm_app.util.Resource
import kotlinx.coroutines.launch

class CountryViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "CountryViewModel"
    private val repository: CountryRepository

    // LiveData from Room
    val countries: LiveData<List<Country>>

    private val _refreshStatus = MutableLiveData<Resource<List<Country>>>()
    val refreshStatus: LiveData<Resource<List<Country>>> = _refreshStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val countryDao = AppDatabase.getDatabase(application).countryDao()
        repository = CountryRepository(countryDao)
        countries = repository.allCountries

        // Auto-fetch if database is empty
        checkAndFetchCountries()
    }

    private fun checkAndFetchCountries() {
        viewModelScope.launch {
            val hasData = repository.hasLocalData()
            if (!hasData) {
                Log.d(TAG, "No local data, fetching from API...")
                refreshCountries()
            } else {
                Log.d(TAG, "Using cached data from Room")
            }
        }
    }

    fun refreshCountries() {
        _isLoading.value = true
        _refreshStatus.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val result = repository.refreshCountries()
                _refreshStatus.value = result
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing countries", e)
                _refreshStatus.value = Resource.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
