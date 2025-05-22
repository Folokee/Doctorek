package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.PatientAppointment
import com.example.doctorek.data.repositories.PatientAppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PatientAppointmentState(
    val appointments: List<PatientAppointment> = emptyList(),
    val filteredAppointments: List<PatientAppointment> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val filter: AppointmentFilter = AppointmentFilter.ALL
)

enum class AppointmentFilter {
    ALL, UPCOMING, COMPLETED, CANCELLED, CONFIRMED, SCHEDULED
}

class PatientAppointmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PatientAppointmentRepository(application.applicationContext)
    
    private val _state = MutableStateFlow(PatientAppointmentState())
    val state: StateFlow<PatientAppointmentState> = _state
    
    private val _uniqueSpecialties = MutableStateFlow<List<String>>(emptyList())
    val uniqueSpecialties: StateFlow<List<String>> = _uniqueSpecialties
    
    private val _selectedSpecialties = MutableStateFlow<Set<String>>(emptySet())
    val selectedSpecialties: StateFlow<Set<String>> = _selectedSpecialties
    
    init {
        fetchPatientAppointments()
    }
    
    fun fetchPatientAppointments() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            
            repository.getPatientAppointments().collect { result ->
                result.fold(
                    onSuccess = { appointments ->
                        val specialties = appointments
                            .mapNotNull { it.doctor_info?.speciality }
                            .distinct()
                            .sorted()
                        _uniqueSpecialties.value = specialties
                        
                        _state.update { 
                            it.copy(
                                appointments = appointments,
                                filteredAppointments = filterAppointments(
                                    appointments, 
                                    it.filter,
                                    _selectedSpecialties.value
                                ),
                                loading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update { 
                            it.copy(
                                loading = false, 
                                error = e.message ?: "Unknown error occurred"
                            )
                        }
                    }
                )
            }
        }
    }
    
    // Method to refresh appointments data
    fun refreshAppointments() {
        fetchPatientAppointments()
    }
    
    fun setFilter(filter: AppointmentFilter) {
        _state.update { 
            it.copy(
                filter = filter,
                filteredAppointments = filterAppointments(
                    it.appointments, 
                    filter,
                    _selectedSpecialties.value
                )
            )
        }
    }
    
    private fun filterAppointments(
        appointments: List<PatientAppointment>, 
        filter: AppointmentFilter,
        selectedSpecialties: Set<String>
    ): List<PatientAppointment> {
        val statusFiltered = when (filter) {
            AppointmentFilter.ALL -> appointments
            AppointmentFilter.UPCOMING -> appointments.filter { 
                it.status.equals("pending", ignoreCase = true) || 
                it.status.equals("confirmed", ignoreCase = true) ||
                it.status.equals("scheduled", ignoreCase = true)
            }
            AppointmentFilter.CONFIRMED -> appointments.filter {
                it.status.equals("confirmed", ignoreCase = true)
            }
            AppointmentFilter.SCHEDULED -> appointments.filter {
                it.status.equals("scheduled", ignoreCase = true)
            }
            AppointmentFilter.COMPLETED -> appointments.filter { 
                it.status.equals("completed", ignoreCase = true) 
            }
            AppointmentFilter.CANCELLED -> appointments.filter { 
                it.status.equals("cancelled", ignoreCase = true) 
            }
        }
        
        return if (selectedSpecialties.isEmpty()) {
            statusFiltered
        } else {
            statusFiltered.filter { appointment ->
                appointment.doctor_info?.speciality?.let { speciality ->
                    selectedSpecialties.contains(speciality)
                } ?: false
            }
        }
    }
    
    fun updateSpecialtyFilters(specialties: Set<String>) {
        _selectedSpecialties.value = specialties
        _state.update { 
            it.copy(
                filteredAppointments = filterAppointments(
                    it.appointments,
                    it.filter,
                    specialties
                )
            )
        }
    }
    
    fun searchAppointments(query: String) {
        if (query.isBlank()) {
            _state.update { 
                it.copy(
                    filteredAppointments = filterAppointments(
                        it.appointments, 
                        it.filter,
                        _selectedSpecialties.value
                    )
                )
            }
            return
        }
        
        val filteredByStatus = filterAppointments(
            _state.value.appointments, 
            _state.value.filter,
            _selectedSpecialties.value
        )
        val searchResults = filteredByStatus.filter { appointment ->
            appointment.doctor_info?.full_name?.contains(query, ignoreCase = true) == true ||
                    appointment.doctor_info?.speciality?.contains(query, ignoreCase = true) == true ||
            appointment.appointment_date.contains(query, ignoreCase = true)
        }
        
        _state.update { it.copy(filteredAppointments = searchResults) }
    }
    
    fun resetFilters() {
        _selectedSpecialties.value = emptySet()
        _state.update { 
            it.copy(
                filter = AppointmentFilter.ALL,
                filteredAppointments = filterAppointments(
                    it.appointments,
                    AppointmentFilter.ALL,
                    emptySet()
                )
            )
        }
    }
}
