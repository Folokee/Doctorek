package com.example.doctorek.data.repositories

import android.content.Context
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.PatientAppointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PatientAppointmentRepository(private val context: Context) {
    private val apiService = ApiClient.apiService
    private val sharedPrefs = SharedPrefs(context)

    fun getPatientAppointments(): Flow<Result<List<PatientAppointment>>> = flow {
        val token = sharedPrefs.getAccess()
        val patientId = sharedPrefs.getUserId()

        if (token == null || patientId == null) {
            emit(Result.failure(Exception("User not authenticated")))
            return@flow
        }

        try {
            val response = apiService.getPatientAppointments(
                patientId = patientId,
                token = "Bearer $token"
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("Empty response")))
            } else {
                emit(Result.failure(Exception("Error fetching appointments: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // Helper functions to parse date and time
    fun parseAppointmentDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }

    fun parseAppointmentTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString)
    }
}
