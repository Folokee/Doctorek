package com.example.doctorek.ui.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.DoctorAvailability
import com.example.doctorek.data.repositories.DoctorRepository
import com.example.doctorek.ui.screens.state.AvailabilityState
import com.example.doctorek.ui.screens.state.DoctorDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Locale

class DoctorDetailViewModel(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    val _state = MutableStateFlow<DoctorDetailState>(DoctorDetailState.Loading)
    val state: StateFlow<DoctorDetailState> = _state

    val _favoriteState = MutableStateFlow(false)
    val favoriteState: StateFlow<Boolean> = _favoriteState

    val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex
    
    // Availability state
    private val _availabilityState = MutableStateFlow<AvailabilityState>(AvailabilityState.Loading)
    val availabilityState: StateFlow<AvailabilityState> = _availabilityState

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDoctorDetails(doctorId: String) {
        _state.value = DoctorDetailState.Loading

        viewModelScope.launch {
            doctorRepository.getDoctorById(doctorId)
                .onSuccess { doctor ->
                    _state.value = DoctorDetailState.Success(doctor)
                    processAvailability(doctor.doctor_availability)
                }
                .onFailure { error ->
                    _state.value = DoctorDetailState.Error(error.message ?: "Unknown error occurred")
                    _availabilityState.value = AvailabilityState.Error("Failed to load availability")
                }
        }
    }

    fun toggleFavorite() {
        _favoriteState.value = !_favoriteState.value
        // In a real app, you would save this preference to a repository
    }

    fun selectDay(index: Int) {
        _selectedDayIndex.value = index
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun processAvailability(doctorAvailability: List<DoctorAvailability>) {
        val availableDays = mutableListOf<DayOfWeek>()
        
        doctorAvailability.forEach { availability ->
            if (availability.is_available) {
                val dayName = availability.day_of_week.uppercase(Locale.ROOT)
                try {
                    // Convert day_of_week string to DayOfWeek enum
                    val dayOfWeek = DayOfWeek.valueOf(dayName)
                    availableDays.add(dayOfWeek)
                } catch (e: IllegalArgumentException) {
                    // Handle invalid day names
                }
            }
        }
        
        _availabilityState.value = if (availableDays.isEmpty()) {
            AvailabilityState.NotAvailable
        } else {
            AvailabilityState.Available(availableDays.distinct())
        }
    }

    // Factory class to create the ViewModel with dependencies
    companion object {
        // Factory class to create the ViewModel with dependencies
        class Factory(private val context: Context) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DoctorDetailViewModel::class.java)) {
                    return DoctorDetailViewModel(DoctorRepository(context)) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
