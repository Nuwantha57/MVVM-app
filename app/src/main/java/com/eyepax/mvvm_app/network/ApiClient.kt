package com.eyepax.mvvm_app.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Replace with YOUR API URL
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // Replace with YOUR certificate hash (get from Step 1)
    private const val CERTIFICATE_HASH = "sha256/mEflZT5enoR1FuXLgYYGqnVEoZvmf9c2bVBpiOjYQ0c="

    /**
     * Create OkHttpClient with SSL Pinning
     */
    private fun createSecureOkHttpClient(): OkHttpClient {
        // Get hostname from URL
        val hostname = BASE_URL
            .replace("https://", "")
            .replace("http://", "")
            .replace("/", "")

        // Create Certificate Pinner
        val certificatePinner = CertificatePinner.Builder()
            .add(hostname, CERTIFICATE_HASH)
            .build()

        // Logging Interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner) // SSL Pinning
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Create Retrofit instance with SSL Pinning
     */
    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createSecureOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Get Retrofit instance - PUBLIC function
     */
    fun provideRetrofit(): Retrofit {
        return retrofitInstance
    }

    /**
     * Get OkHttpClient from Retrofit
     */
    fun provideOkHttpClient(): OkHttpClient {
        return retrofitInstance.callFactory() as OkHttpClient
    }
}
