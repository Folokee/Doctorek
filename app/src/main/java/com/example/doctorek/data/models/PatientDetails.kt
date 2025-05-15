package com.example.doctorek.data.models

data class PatientDetails(
    val fullName: String = "",
    val ageRange: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val problem: String = ""
)

// Age range options
object AgeRanges {
    val ranges = listOf("1 - 17", "18 - 24", "25 - 45", "46 - 75+")
}

// Gender options
object GenderOptions {
    val options = listOf("Male", "Female", "Other", "Prefer not to say")
}