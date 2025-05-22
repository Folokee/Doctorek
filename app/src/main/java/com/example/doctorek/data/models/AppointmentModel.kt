package com.example.doctorek.data.models

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class AppointmentModel(
    val id: String = UUID.randomUUID().toString(),
    val patient_name: String?,
    val patient_id: String,
    val appointment_date: String?,
    val start_time: String?,
    val end_time: String?,
    val status: String?,
    val reason: String?,
    val notes: String? = null
) {
    // Helper function to get the appointment date as LocalDate
    fun getAppointmentDate(): LocalDate {
        return LocalDate.parse(appointment_date, DateTimeFormatter.ISO_DATE)
    }

    // Helper function to check if the appointment is upcoming
    fun isUpcoming(): Boolean {
        val today = LocalDate.now()
        val now = LocalTime.now()

        return getAppointmentDate().isAfter(today) ||
                (getAppointmentDate().isEqual(today) &&
                        LocalTime.parse(start_time).isAfter(now))
    }
}

data class UpdateAppStatus(
    val appointment_id : String,
    val status : String,
)

