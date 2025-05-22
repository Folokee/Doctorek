package com.example.doctorek.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.doctorek.data.api.ApiClient.apiService
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.database.AppDatabase
import com.example.doctorek.data.entities.PrescriptionEntity
import com.example.doctorek.data.models.PatientInfo
import com.example.doctorek.data.models.PrescriptionModel
import com.example.doctorek.data.models.SavePrescriptionModel
import com.example.doctorek.ui.screens.doctorScreens.Medication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrescriptionsRepository(private val context: Context) {
    private val sharedPrefs = SharedPrefs(context)
    private val prescriptionDao = AppDatabase.getInstance(context).prescriptionDao()

    suspend fun getPrescriptions(): Result<List<PrescriptionModel>> {
        val token = sharedPrefs.getAccess()
        return try {
            val response = apiService.getPrescriptions("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error fetching prescriptions"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }
    }


    suspend fun savePrescription(
        appointmentId: String?,
        patientId: String,
        medications: List<Medication>,
        additionalNotes: String
    ): Result<Long> {
        // Create entity to save locally
        var prescriptionEntity = PrescriptionEntity(
            appointment_id = appointmentId,
            patient_id = patientId,
            details = medications,
            additional_notes = additionalNotes,
            is_synced = false
        )

        // If online, try to save to server first
        if (isNetworkAvailable()) {
            try {
                val token = sharedPrefs.getAccess() ?: return Result.failure(Exception("Authentication token not found"))

                val savePrescriptionModel = SavePrescriptionModel(
                    appointment_id = appointmentId ?: "",
                    patient_id = patientId,
                    details = medications,
                    additional_notes = additionalNotes
                )

                val response = apiService.createPrescription("Bearer $token", savePrescriptionModel)

                if (response.isSuccessful) {
                    // If successful, mark as synced
                    prescriptionEntity.is_synced = true
                }
            } catch (e: Exception) {
                // Failed to save online - will save locally only
            }
        }

        // Save locally (whether synced or not)
        return try {
            val id = withContext(Dispatchers.IO) {
                prescriptionDao.insert(prescriptionEntity)
            }
            Log.d("PrescriptionsRepository", "Saved prescription locally with ID: $id")
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to save prescription locally: ${e.message}"))
        }
    }

    private suspend fun syncLocalPrescriptions() {
        if (!isNetworkAvailable()) return

        val token = sharedPrefs.getAccess() ?: return

        try {
            val unsyncedPrescriptions = withContext(Dispatchers.IO) {
                prescriptionDao.getUnsyncedPrescriptions()
            }

            for (prescription in unsyncedPrescriptions) {
                val savePrescriptionModel = SavePrescriptionModel(
                    appointment_id = prescription.appointment_id ?: "",
                    patient_id = prescription.patient_id,
                    details = prescription.details,
                    additional_notes = prescription.additional_notes
                )

                val response = apiService.createPrescription(token, savePrescriptionModel)

                if (response.isSuccessful) {
                    // Update local entity to mark as synced
                    prescription.is_synced = true
                    withContext(Dispatchers.IO) {
                        prescriptionDao.update(prescription)
                    }
                }
            }
        } catch (e: Exception) {
            // Failed to sync - will try again next time
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}