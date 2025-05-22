package com.example.doctorek.data.repositories

import android.content.Context
import com.example.doctorek.data.api.ApiClient.apiService
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.AppointmentModel
import com.example.doctorek.data.models.UpdateAppStatus


class AppointmentsRepository(private val context : Context){
    private val sharedPrefs = SharedPrefs(context)

    suspend fun getAppointments(): Result<List<AppointmentModel>> {
        val token = sharedPrefs.getAccess()
        return try {
            val response = apiService.getDoctorAppointments(token!!)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error fetching appointments"))
            }
        } catch (e : Exception){
            Result.failure(Exception(e.message))
        }

    }

    suspend fun updateStatus(
        appointmentId: String,
        status: String
    ): Result<String> {
        val token = sharedPrefs.getAccess()
        return try {
            val response = apiService.updateAppointmentStatus("Bearer $token",UpdateAppStatus(
                appointmentId,
                status
            ))
            if (response.isSuccessful) {
                Result.success("Status updated successfully")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error updating status"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }
    }
}