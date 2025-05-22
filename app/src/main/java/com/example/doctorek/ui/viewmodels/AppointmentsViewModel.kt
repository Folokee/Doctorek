package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.AppointmentModel
import com.example.doctorek.data.repositories.AppointmentsRepository
import com.example.doctorek.ui.screens.doctorScreens.Appointment
import com.example.doctorek.ui.screens.doctorScreens.AppointmentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.collections.forEach

data class uiState(
    val appointments: List<Appointment> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class AppointmentsViewModel(application: Application) : AndroidViewModel(application){
    private val repository = AppointmentsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(uiState())
    val uiState : StateFlow<uiState> = _uiState.asStateFlow()

    init {
        getAllAppointments()
    }


    private fun getAllAppointments() {
        viewModelScope.launch {
            _uiState.value = uiState(loading = true)
            try {
                val appointments = repository.getAppointments()
                if (appointments.isSuccess){
                    var refactApps = emptyList<Appointment>()
                    appointments.getOrNull()?.forEach {appointment ->
                        refactApps =  refactApps.plus(
                            Appointment(
                                id = appointment.id,
                                patientName = appointment.patient_name?: "Unknown",
                                date = appointment.getAppointmentDate(),
                                startTime = LocalTime.parse(appointment.start_time),
                                endTime = LocalTime.parse(appointment.end_time),
                                state = when (appointment.status) {
                                    "scheduled" -> AppointmentState.SCHEDULED
                                    "completed" -> AppointmentState.DONE
                                    "cancelled" -> AppointmentState.CANCELLED
                                    "confirmed" -> AppointmentState.CONFIRMED
                                    "no_show" -> AppointmentState.MISSED
                                    else -> AppointmentState.SCHEDULED
                                }
                            )
                        )
                    }
                    _uiState.value = uiState(appointments = refactApps, loading = false)
                }else {
                    _uiState.value = uiState(error = appointments.exceptionOrNull()?.message, loading = false)
                }
            } catch (e: Exception) {
                _uiState.value = uiState(error = e.message, loading = false)
            }
        }
    }

    fun updateStatus(appointmentId : String, status : String){
        viewModelScope.launch {
            _uiState.value = uiState(loading = true)
            try {
                val result = repository.updateStatus(appointmentId, status)
                if (result.isSuccess){
                    getAllAppointments()
                }else {
                    _uiState.value = uiState(error = result.exceptionOrNull()?.message, loading = false)
                }
            } catch (e: Exception) {
                _uiState.value = uiState(error = e.message, loading = false)
            }
        }
    }
}