package com.example.doctorek.data.models

data class DoctorAvailabilityResponse(
    val doctor_id: String,
    val date: String,
    val day_of_week: String,
    val available_slots: List<AvailableSlot>
)

data class AvailableSlot(
    val day_of_week: String,
    val start_time: String,
    val end_time: String,
    val duration: Int
)
