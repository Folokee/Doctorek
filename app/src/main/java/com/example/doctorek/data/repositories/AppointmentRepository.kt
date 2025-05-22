package com.example.doctorek.data.repositories

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.api.ApiService
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.AppointmentRequest
import com.example.doctorek.data.models.AppointmentResponse
import com.example.doctorek.data.models.AppointmentSlot
import com.example.doctorek.data.models.AppointmentTimeSection
import com.example.doctorek.data.models.DoctorAvailabilityResponse
import com.example.doctorek.data.models.TimePeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AppointmentRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.apiService
    private val sessionManager = SharedPrefs(context)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAvailableTimeSlots(doctorId: String, date: LocalDate): Result<List<AppointmentTimeSection>> {
        return withContext(Dispatchers.IO) {
            try {
                val dateStr = date.toString()
                val token = sessionManager.getAccess() ?: ""
                
                val response = apiService.getDoctorAvailability(doctorId, dateStr, "Bearer $token")
                if (response.isSuccessful) {
                    val availabilityResponse = response.body()
                    if (availabilityResponse != null) {
                        // Convert API response to UI model
                        val timeSections = convertToTimeSections(availabilityResponse)
                        Result.success(timeSections)
                    } else {
                        Result.failure(Exception("No availability data returned"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch availability: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToTimeSections(availabilityResponse: DoctorAvailabilityResponse): List<AppointmentTimeSection> {
        val morningSlots = mutableListOf<AppointmentSlot>()
        val eveningSlots = mutableListOf<AppointmentSlot>()
        
        val availableSlots = availabilityResponse.available_slots
        
        for (slot in availableSlots) {
            // Parse start and end times
            val startTime = LocalTime.parse(slot.start_time)
            val endTime = LocalTime.parse(slot.end_time)
            
            // Calculate slot intervals based on duration
            val durationMinutes = slot.duration
            val slotDurationMinutes = 30 // Default to 30-minute slots
            
            var currentTime = startTime
            while (currentTime.plusMinutes(slotDurationMinutes.toLong()) <= endTime) {
                val appointmentSlot = AppointmentSlot(
                    time = currentTime,
                    isAvailable = true
                )
                
                // Categorize as morning or evening
                if (currentTime.hour < 12) {
                    morningSlots.add(appointmentSlot)
                } else {
                    eveningSlots.add(appointmentSlot)
                }
                
                currentTime = currentTime.plusMinutes(slotDurationMinutes.toLong())
            }
        }
        
        return listOf(
            AppointmentTimeSection(TimePeriod.MORNING, morningSlots),
            AppointmentTimeSection(TimePeriod.EVENING, eveningSlots)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMockTimeSlots(): List<AppointmentTimeSection> {
        // Mock time slots for testing
        val morningSlots = (8..11).map { hour ->
            AppointmentSlot(LocalTime.of(hour, 0), true)
        } + (8..11).map { hour ->
            AppointmentSlot(LocalTime.of(hour, 30), hour != 9)
        }

        val eveningSlots = (12..17).map { hour ->
            AppointmentSlot(LocalTime.of(hour, 0), hour != 15)
        } + (12..17).map { hour ->
            AppointmentSlot(LocalTime.of(hour, 30), true)
        }

        return listOf(
            AppointmentTimeSection(TimePeriod.MORNING, morningSlots.sortedBy { it.time }),
            AppointmentTimeSection(TimePeriod.EVENING, eveningSlots.sortedBy { it.time })
        )
    }

    suspend fun bookAppointment(request: AppointmentRequest): Result<AppointmentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.bookAppointment(request)
                if (response.isSuccessful) {
                    val appointmentResponse = response.body()
                    if (appointmentResponse != null) {
                        Result.success(appointmentResponse)
                    } else {
                        Result.failure(Exception("No response data"))
                    }
                } else {
                    Result.failure(Exception("Failed to book appointment: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createAppointment(request: AppointmentRequest): Result<AppointmentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAccess() ?: ""
                val response = apiService.createAppointment(request, "Bearer $token")
                
                if (response.isSuccessful) {
                    val appointmentResponse = response.body()
                    if (appointmentResponse != null) {
                        Result.success(appointmentResponse)
                    } else {
                        Result.failure(Exception("No response data"))
                    }
                } else {
                    Result.failure(Exception("Failed to book appointment: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
