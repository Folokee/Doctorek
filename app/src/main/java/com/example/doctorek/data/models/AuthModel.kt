package com.example.doctorek.data.models

data class SigninRequest(
    val email: String,
    val password: String
)

data class SigninResponse(
    val success : Boolean,
    val message: String,
    val data: userData
)

data class userData(
    val access_token: String,
    val refresh_token: String,
    val userId: String
)
data class SignupResponse(
    val success : Boolean,
    val message: String,
    val data: userData
)

data class SignupRequest(
    val email: String,
    val password: String,
    val user_type: String,
)