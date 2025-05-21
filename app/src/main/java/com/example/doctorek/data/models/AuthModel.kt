package com.example.doctorek.data.models

data class SigninRequest(
    val email: String,
    val password: String
)

data class SigninResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)