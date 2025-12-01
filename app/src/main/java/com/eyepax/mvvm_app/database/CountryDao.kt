package com.eyepax.mvvm_app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eyepax.mvvm_app.model.Country

@Dao
interface CountryDao {

    @Query("SELECT * FROM countries ORDER BY name")
    fun getAllCountries(): LiveData<List<Country>>

    @Query("SELECT * FROM countries WHERE code = :code")
    suspend fun getCountryByCode(code: String): Country?

    @Query("SELECT * FROM countries WHERE region = :region ORDER BY name")
    fun getCountriesByRegion(region: String): LiveData<List<Country>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<Country>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: Country)

    @Query("DELETE FROM countries")
    suspend fun deleteAllCountries()

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun getCountryCount(): Int
}
