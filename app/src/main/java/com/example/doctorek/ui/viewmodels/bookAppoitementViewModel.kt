package com.example.doctorek.ui.viewmodels


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.AppointmentRequest
import com.example.doctorek.data.models.PatientDetails
import com.example.doctorek.data.models.TimePeriod
import com.example.doctorek.data.repositories.AppointmentRepository
import com.example.doctorek.ui.screens.state.BookAppointmentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
open class BookAppointmentViewModel(
    private val doctorId: String,
    private val selectedDate: LocalDate,
    private val repository: AppointmentRepository
) : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val _state = MutableStateFlow(
        BookAppointmentState(
            doctorId = doctorId,
            date = selectedDate
        )
    )
    // Existing state properties...

    // Add dialog state for appointment result
    private val _appointmentStatus = MutableStateFlow<AppointmentStatus>(AppointmentStatus.Initial)
    val appointmentStatus: StateFlow<AppointmentStatus> = _appointmentStatus

    // Add loading state
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    // Define the status enum
    sealed class AppointmentStatus {
        object Initial : AppointmentStatus()
        object Success : AppointmentStatus()
        object Failure : AppointmentStatus()
    }
    // New state for patient details - simplified
    private val _patientDetails = MutableStateFlow(PatientDetails())
    val patientDetails: StateFlow<PatientDetails> = _patientDetails

    @RequiresApi(Build.VERSION_CODES.O)
    val state: StateFlow<BookAppointmentState> = _state

    init {
        loadAvailableTimeSlots()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAvailableTimeSlots() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // For production use real API data
                val result = repository.getAvailableTimeSlots(doctorId, state.value.date)
                result.onSuccess { timeSections ->
                    _state.update { it.copy(
                        isLoading = false,
                        availableTimeSections = timeSections
                    )}
                }.onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = error.message
                    )}
                }

                // Comment this out when using real API
                // val timeSections = repository.getMockTimeSlots()
                // _state.update { it.copy(
                //     isLoading = false,
                //     availableTimeSections = timeSections
                // )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message
                )}
            }
        }
    }

    // Reset appointment status
    fun resetAppointmentStatus() {
        _appointmentStatus.value = AppointmentStatus.Initial
    }
    // Update functions for the simplified form
    fun updateProblem(problem: String) {
        _patientDetails.update { it.copy(problem = problem) }
    }

    fun updateAdditionalNotes(notes: String) {
        _patientDetails.update { it.copy(additionalNotes = notes) }
    }

    fun validatePatientDetails(): Boolean {
        // Only validate that problem is not empty
        return _patientDetails.value.problem.isNotBlank()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectTimePeriod(period: TimePeriod) {
        _state.update { it.copy(selectedTimePeriod = period) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectTime(time: LocalTime) {
        _state.update { it.copy(selectedTime = time) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitAppointment(patientId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentState = _state.value
        val selectedTime = currentState.selectedTime
        val currentDetails = _patientDetails.value

        // Validate time selection
        if (selectedTime == null) {
            onError("Please select a time slot")
            return
        }

        // Validate patient details
        if (!validatePatientDetails()) {
            onError("Please provide a reason for your visit")
            return
        }

        _state.update { it.copy(isLoading = true) }
        _isSubmitting.value = true

        viewModelScope.launch {
            try {
                // Calculate end time (assuming 1-hour appointments)
                val startTimeStr = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                val endTimeStr = selectedTime.plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                
                // Format the date
                val dateStr = currentState.date.toString()
                
                // Create appointment request with the new format
                val request = AppointmentRequest(
                    doctor_id = currentState.doctorId,
                    appointment_date = dateStr,
                    start_time = startTimeStr,
                    end_time = endTimeStr,
                    reason = currentDetails.problem,
                    notes = currentDetails.additionalNotes ?: ""
                )

                repository.createAppointment(request)
                    .onSuccess {
                        _appointmentStatus.value = AppointmentStatus.Success
                        _isSubmitting.value = false
                        _state.update { it.copy(isLoading = false) }
                        // Don't call onSuccess here, let the UI observe the state change
                    }
                    .onFailure { error ->
                        _appointmentStatus.value = AppointmentStatus.Failure
                        _isSubmitting.value = false
                        _state.update { it.copy(
                            isLoading = false,
                            error = error.message
                        )}
                        onError(error.message ?: "Failed to book appointment")
                    }
            } catch (e: Exception) {
                _appointmentStatus.value = AppointmentStatus.Failure
                _isSubmitting.value = false
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message
                )}
                onError(e.message ?: "An unexpected error occurred")
            }
        }
    }

    class Factory(
        private val doctorId: String,
        private val selectedDate: LocalDate,
        private val repository: AppointmentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookAppointmentViewModel::class.java)) {
                return BookAppointmentViewModel(doctorId, selectedDate, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

        companion object {
            fun create(
                doctorId: String,
                selectedDate: LocalDate,
                context: Context
            ): Factory {
                return Factory(
                    doctorId,
                    selectedDate,
                    AppointmentRepository(context)
                )
            }
        }
    }
}