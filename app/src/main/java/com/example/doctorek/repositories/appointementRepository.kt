package com.example.tdm_project.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import  com.example.tdm_project.models.AppointmentRequest
import  com.example.tdm_project.models.AppointmentResponse
import com.example.tdm_project.models.AppointmentSlot
import com.example.tdm_project.models.AppointmentTimeSection
import kotlinx.coroutines.Dispatchers
import com.example.tdm_project.api.RetrofitClient
import com.example.tdm_project.models.TimePeriod
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AppointmentRepository {
    private val apiService = RetrofitClient.appointmentApiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAvailableTimeSlots(doctorId: String, date: LocalDate): Result<List<AppointmentTimeSection>> {
        return withContext(Dispatchers.IO) {
            try {
                val dateStr = date.format(DateTimeFormatter.ISO_DATE)
                val response = apiService.getAvailableSlots(doctorId, dateStr)

                if (response.isSuccessful) {
                    val availableTimes = response.body() ?: emptyMap()
                    val result = processAvailableTimes(availableTimes)
                    Result.success(result)
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun bookAppointment(request: AppointmentRequest): Result<AppointmentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.bookAppointment(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Process the API response into our domain model
    @RequiresApi(Build.VERSION_CODES.O)
    private fun processAvailableTimes(availableTimes: Map<String, List<String>>): List<AppointmentTimeSection> {
        val morningSlots = mutableListOf<AppointmentSlot>()
        val eveningSlots = mutableListOf<AppointmentSlot>()

        // Morning slots: 9 AM to 12 PM
        val morningTimes = listOf("09:00", "10:00", "11:00")

        // Evening slots: 1 PM to 7 PM
        val eveningTimes = listOf("13:00", "14:00", "15:00", "17:00", "18:00", "19:00")

        val availableSlots = availableTimes["available_slots"] ?: emptyList()

        for (time in morningTimes) {
            val isAvailable = availableSlots.contains(time)
            morningSlots.add(
                AppointmentSlot(
                    time = LocalTime.parse(time),
                    isAvailable = isAvailable
                )
            )
        }

        for (time in eveningTimes) {
            val isAvailable = availableSlots.contains(time)
            eveningSlots.add(
                AppointmentSlot(
                    time = LocalTime.parse(time),
                    isAvailable = isAvailable
                )
            )
        }

        return listOf(
            AppointmentTimeSection(TimePeriod.MORNING, morningSlots),
            AppointmentTimeSection(TimePeriod.EVENING, eveningSlots)
        )
    }

    // For development and testing
    @RequiresApi(Build.VERSION_CODES.O)
    open fun getMockTimeSlots(): List<AppointmentTimeSection> {
        val morningSlots = listOf(
            AppointmentSlot(LocalTime.of(9, 0)),
            AppointmentSlot(LocalTime.of(10, 0)),
            AppointmentSlot(LocalTime.of(11, 0))
        )

        val eveningSlots = listOf(
            AppointmentSlot(LocalTime.of(13, 0)),
            AppointmentSlot(LocalTime.of(14, 0)),
            AppointmentSlot(LocalTime.of(15, 0)),
            AppointmentSlot(LocalTime.of(17, 0)),
            AppointmentSlot(LocalTime.of(18, 0)),
            AppointmentSlot(LocalTime.of(19, 0))
        )

        return listOf(
            AppointmentTimeSection(TimePeriod.MORNING, morningSlots),
            AppointmentTimeSection(TimePeriod.EVENING, eveningSlots)
        )
    }
}