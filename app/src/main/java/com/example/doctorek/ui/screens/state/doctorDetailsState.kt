package com.example.doctorek.ui.screens.state

import com.example.doctorek.data.models.DoctorDetailResponse


sealed class DoctorDetailState {
    object Loading : DoctorDetailState()
    data class Success(val doctor: DoctorDetailResponse) : DoctorDetailState()
    data class Error(val message: String) : DoctorDetailState()
}