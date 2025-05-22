package com.example.doctorek.data.repositories

import android.content.Context
import com.example.doctorek.data.api.ApiClient.apiService
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.database.AppDatabase
import com.example.doctorek.data.entities.PatientEntity
import com.example.doctorek.data.models.PatientModel

class PatientsRepository(private val context: Context){
    private val sharedPrefs = SharedPrefs(context)
    private val patientDao = AppDatabase.getInstance(context).patientDao()

    suspend fun getPatients(): Result<String> {
        return try {
            val token = sharedPrefs.getAccess()
            val response = apiService.getPatients("Bearer $token")

            if (response.isSuccessful) {
                val patients = response.body() ?: emptyList()
                // Save patients to local database
                var entities = patients.map {
                    PatientEntity(
                        patient_id = it.patient_id,
                        full_name = it.full_name,
                        email = it.email
                    )
                }
                patientDao.deleteAll()
                patientDao.insertAll(entities)
                Result.success("${patients.size} patients found")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error fetching patients"))
            }
        } catch (e : Exception){
            Result.failure(Exception(e.message))
        }
    }
}