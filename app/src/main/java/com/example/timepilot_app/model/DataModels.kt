package com.example.timepilot_app.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val access_token: String,
    val refresh_token: String,
    val code: Int,
    val message: String
)

data class EmailCodeResponse(
    val code: String,
    val info: String,
    val data: Boolean
)

// 请求体
data class RefreshTokenRequest(
    val refresh_token: String
)

// 响应体
data class RefreshTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val code: Int,
    val message: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val verificationCode: String,
    val registerType: String = "EMAIL"
)

data class RegisterResponse(
    val code: Int,
    val message: String
)