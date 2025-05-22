package com.example.doctorek.data.models


data class ProfileModel(
    val email : String? = "",
    val phone_number : String? = "",
    val full_name : String? = "",
    val address : String? = "",
    val avatar_url : String? = "",
)

data class UpdateProfileModel(
    val phone_number : String? = "",
    val full_name : String? = "",
    val address : String? = "",
    val avatar_url : String? = "",
)

data class ProfileResponse(
    val success : Boolean,
    val message: String,
    val data : ProfileModel
)
