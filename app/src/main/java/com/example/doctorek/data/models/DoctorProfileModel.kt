package com.example.doctorek.data.models

data class DoctorProfileModel(
    val specialty: String,
    val hospital_name: String,
    val hospital_address: String,
    val bio: String,
    val years_of_experience: Int,
    val contact_information: ContactInfo,
)

data class ContactInfo(
    val phone: String
)