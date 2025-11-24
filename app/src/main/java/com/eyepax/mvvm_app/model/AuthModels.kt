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

// Sign Up Request
data class SignUpRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,
)

// Sign Up Response
data class SignUpResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("userConfirmed")
    val userConfirmed: Boolean? = false
)

// Generic Error Response
data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)

// Verification Request (NEW)
data class VerifyEmailRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("code")
    val code: String
)

// Verification Response (NEW)
data class VerifyEmailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)

// Resend Code Request (NEW)
data class ResendCodeRequest(
    @SerializedName("username")
    val username: String
)