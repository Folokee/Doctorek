package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.repositories.PrescriptionsRepository
import com.example.doctorek.ui.screens.doctorScreens.Medication
import com.example.doctorek.ui.screens.doctorScreens.Prescription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrescriptionsState(
    val prescriptions: List<Prescription> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class PrescriptionsViewModel(application: Application) : AndroidViewModel(application){
    private val repository = PrescriptionsRepository(application.applicationContext)
    private val sharedPrefs = SharedPrefs(application.applicationContext)

    private val _uiState = MutableStateFlow(PrescriptionsState())
    val uiState: StateFlow<PrescriptionsState> = _uiState.asStateFlow()

    init {
        getAllPrescriptions()
    }

    fun getAllPrescriptions() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            repository.getPrescriptions().onSuccess { prescriptions ->
                val refactPrescriptions = prescriptions.map { prescription ->
                    Prescription(
                        id = prescription.id,
                        patientName = prescription.patient.name,
                        date = prescription.getPrescriptionDate(),
                        medications = prescription.details,
                    )
                }
                _uiState.update { it.copy(prescriptions = refactPrescriptions, loading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, loading = false) }
            }
        }
    }

    fun savePrescription(
        appointmentId: String?,
        patientId: String,
        medications: List<Medication>,
        additionalNotes: String = ""
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            repository.savePrescription(
                appointmentId = appointmentId,
                patientId = patientId,
                medications = medications,
                additionalNotes = additionalNotes
            ).onSuccess {
                _uiState.update { it.copy(loading = false, saveSuccess = true) }
                getAllPrescriptions() // Refresh the list
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, loading = false) }
            }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}