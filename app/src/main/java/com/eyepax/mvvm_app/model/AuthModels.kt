package com.eyepax.mvvm_app.model

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)

// Login Response
data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("idToken")
    val idToken: String? = null,

    @SerializedName("expiresIn")
    val expiresIn: Int? = null
)

// Generic Error Response
data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)
