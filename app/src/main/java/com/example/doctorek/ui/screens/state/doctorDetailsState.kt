package com.example.doctorek.ui.screens.state

import com.example.doctorek.data.models.DoctorModel


sealed class DoctorDetailState {
    object Loading : DoctorDetailState()
    data class Success(val doctor: DoctorModel) : DoctorDetailState()
    data class Error(val message: String) : DoctorDetailState()
}