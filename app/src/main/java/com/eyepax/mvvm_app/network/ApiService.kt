package com.eyepax.mvvm_app.network

import com.eyepax.mvvm_app.model.LoginRequest
import com.eyepax.mvvm_app.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
