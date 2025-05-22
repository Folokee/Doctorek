package com.example.doctorek.data.repositories

import android.content.Context
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.PatientPrescription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException

class PrescriptionRepository(private val context: Context) {
    private val apiService = ApiClient.apiService
    private val sharedPrefs = SharedPrefs(context)
    
    fun getPatientPrescriptions(): Flow<Result<List<PatientPrescription>>> = flow {
        try {
            val token = "Bearer ${sharedPrefs.getAccess()}"
            val patientId = sharedPrefs.getUserId()

            if (patientId != null) {
                if (patientId.isNotEmpty()) {
                    val response = patientId?.let { apiService.getPatientPrescriptions(it, token) }

                    if (response != null) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                emit(Result.success(it))
                            } ?: emit(Result.failure(IOException("Response body is null")))
                        } else {
                            emit(Result.failure(HttpException(response)))
                        }
                    }
                } else {
                    emit(Result.failure(IOException("User ID not found")))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
