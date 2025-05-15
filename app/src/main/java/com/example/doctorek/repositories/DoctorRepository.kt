package com.example.doctorek.repositories
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.models.DoctorModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DoctorRepository {
    private val apiService = ApiClient.doctorApiService

    suspend fun getDoctorById(doctorId: String): Result<DoctorModel> {
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