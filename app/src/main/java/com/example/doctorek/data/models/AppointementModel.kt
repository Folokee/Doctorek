package com.example.doctorek.data.models

import java.time.LocalDate
import java.time.LocalTime

data class AppointmentSlot(
    val time: LocalTime,
    val isAvailable: Boolean = true
)

data class AppointmentTimeSection(
    val period: TimePeriod,
    val slots: List<AppointmentSlot>
)

enum class TimePeriod {
    MORNING, EVENING
}

data class AppointmentRequest(
    val doctor_id: String,
    val appointment_date: String,
    val start_time: String,
    val end_time: String,
    val reason: String,
    val notes: String = "",
    // Keep the following fields for compatibility with existing code
    val patientId: String? = null,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val patientDetails: PatientDetails? = null
)

data class AppointmentResponse(
    val appointmentId: String,
    val status: String,
    val message: String
)