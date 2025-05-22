package com.example.doctorek.ui.screens.state

import com.example.doctorek.data.models.DoctorDetailResponse
import java.time.DayOfWeek

sealed class DoctorDetailState {
    object Loading : DoctorDetailState()
    data class Success(val doctor: DoctorDetailResponse) : DoctorDetailState()
    data class Error(val message: String) : DoctorDetailState()
}

// New state for availability - will be used in the UI
sealed class AvailabilityState {
    object Loading : AvailabilityState()
    data class Available(val availableDays: List<DayOfWeek>) : AvailabilityState()
    object NotAvailable : AvailabilityState()
    data class Error(val message: String) : AvailabilityState()
}