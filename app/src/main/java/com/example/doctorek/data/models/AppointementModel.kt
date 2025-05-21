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
    val doctorId: String,
    val patientId: String,
    val date: LocalDate,
    val time: LocalTime,
    val patientDetails: PatientDetails? = null
)

data class AppointmentResponse(
    val appointmentId: String,
    val status: String,
    val message: String
)