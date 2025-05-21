package com.example.doctorek.ui.viewmodels


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.AgeRanges
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
    // New state for patient details
    private val _patientDetails = MutableStateFlow(PatientDetails())
    val patientDetails: StateFlow<PatientDetails> = _patientDetails

    // Selected age range index
    private val _selectedAgeRangeIndex = MutableStateFlow<Int?>(null)
    val selectedAgeRangeIndex: StateFlow<Int?> = _selectedAgeRangeIndex
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
                // For development, use mock data
                val timeSections = repository.getMockTimeSlots()

                // For production, uncomment this
                // val result = repository.getAvailableTimeSlots(doctorId, state.value.date)
                // result.onSuccess { timeSections ->
                //    _state.update { it.copy(
                //        isLoading = false,
                //        availableTimeSections = timeSections
                //    )}
                // }.onFailure { error ->
                //    _state.update { it.copy(
                //        isLoading = false,
                //        error = error.message
                //    )}
                // }

                _state.update { it.copy(
                    isLoading = false,
                    availableTimeSections = timeSections
                )}
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
    // New functions for patient details form
    fun updateFullName(name: String) {
        _patientDetails.update { it.copy(fullName = name) }
    }

    fun selectAgeRange(index: Int) {
        _selectedAgeRangeIndex.value = index
        _patientDetails.update { it.copy(ageRange = AgeRanges.ranges[index]) }
    }

    fun updatePhoneNumber(phone: String) {
        _patientDetails.update { it.copy(phoneNumber = phone) }
    }

    fun updateGender(gender: String) {
        _patientDetails.update { it.copy(gender = gender) }
    }

    fun updateProblem(problem: String) {
        _patientDetails.update { it.copy(problem = problem) }
    }

    fun validatePatientDetails(): Boolean {
        val currentDetails = _patientDetails.value
        return currentDetails.fullName.isNotBlank() &&
                currentDetails.ageRange.isNotBlank() &&
                currentDetails.phoneNumber.isNotBlank() &&
                currentDetails.gender.isNotBlank()
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
            onError("Please fill in all required fields")
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val request = AppointmentRequest(
                doctorId = currentState.doctorId,
                patientId = patientId,
                date = currentState.date,
                time = selectedTime,
                patientDetails = currentDetails
            )

            // For development, mock success or failure
//            delay(1500) // Simulate network delay

            // Uncomment one of these for testing
            _appointmentStatus.value = AppointmentStatus.Success
            // _appointmentStatus.value = AppointmentStatus.Failure

            _isSubmitting.value = false

            // For production:
            /*
            repository.bookAppointment(request)
                .onSuccess {
                    _appointmentStatus.value = AppointmentStatus.Success
                    _isSubmitting.value = false
                }
                .onFailure { error ->
                    _appointmentStatus.value = AppointmentStatus.Failure
                    _isSubmitting.value = false
                }
            */

            // For development, mock success
            _state.update { it.copy(isLoading = false) }
            onSuccess()

            // For production, uncomment this

            repository.bookAppointment(request)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = error.message
                    )}
                    onError(error.message ?: "Failed to book appointment")
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
    }
}