package com.example.doctorek.data.repositories

import android.content.Context
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.api.ApiConfig
import com.example.doctorek.data.api.ApiService
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.models.DoctorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class DoctorRepository (private val context : Context) {
    private val apiService = ApiClient.apiService
    private val sharedPrefs = SharedPrefs(context)

    fun getDoctors(): Flow<Result<List<DoctorResponse>>> = flow {
        val token = sharedPrefs.getAccess()
        val apiKey = ApiConfig.S_API_KEY
        try {
            val response = apiService.getDoctors("Bearer $token", apiKey)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("Empty response")))
            } else {
                emit(Result.failure(Exception("Error fetching doctors: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getDoctorDetails(doctorId: String): Flow<Result<DoctorDetailResponse>> = flow {
        val token = sharedPrefs.getAccess()
        val apiKey = ApiConfig.S_API_KEY

        try {
            val response = apiService.getDoctorDetails("Bearer $token", apiKey, doctorId)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.isNotEmpty()) {
                        emit(Result.success(it[0]))
                    } else {
                        emit(Result.failure(Exception("Doctor not found")))
                    }
                } ?: emit(Result.failure(Exception("Empty response")))
            } else {
                emit(Result.failure(Exception("Error fetching doctor details: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getDoctorById(doctorId: String): Result<DoctorDetailResponse> {
        return withContext(Dispatchers.IO) {
            val token = sharedPrefs.getAccess()
            try {
                val response = apiService.getDoctorById(doctorId, "Bearer $token")
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