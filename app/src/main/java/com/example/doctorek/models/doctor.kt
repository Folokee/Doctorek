package com.example.tdm_project.models

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val profileImage: String,
    val patientsCount: String,
    val experience: String,
    val rating: String,
    val about: String,
    val phoneNumber: String,
    val email: String,
    val workingHours: String,
    val hospital: Hospital
)

data class Hospital(
    val name: String,
    val address: String,
    val mapLocation: String
)