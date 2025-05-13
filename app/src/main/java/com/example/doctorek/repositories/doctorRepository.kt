package com.example.tdm_project.repositories

import com.example.tdm_project.api.RetrofitClient
import com.example.tdm_project.models.Doctor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DoctorRepository {
    private val apiService = RetrofitClient.doctorApiService

    suspend fun getDoctorById(doctorId: String): Result<Doctor> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDoctorById(doctorId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Doctor data is null"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}