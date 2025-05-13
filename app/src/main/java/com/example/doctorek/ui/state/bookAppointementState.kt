package com.example.tdm_project.ui.state

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tdm_project.models.AppointmentTimeSection
import com.example.tdm_project.models.TimePeriod
import java.time.LocalDate
import java.time.LocalTime

data class BookAppointmentState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val isLoading: Boolean = false,
    val error: String? = null,
    val date: LocalDate = LocalDate.now(),
    val doctorId: String = "",
    val availableTimeSections: List<AppointmentTimeSection> = emptyList(),
    val selectedTimePeriod: TimePeriod = TimePeriod.MORNING,
    val selectedTime: LocalTime? = null
)