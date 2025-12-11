package com.eyepax.mvvm_app.network

import com.eyepax.mvvm_app.model.Country
import retrofit2.Response
import retrofit2.http.GET

interface CountryApiService {

    @GET("v3.1/all?fields=name,cca3,capital,region,subregion,population,area")
    suspend fun getAllCountries(): Response<List<Country>>
}
