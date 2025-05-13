package com.example.tdm_project.ui.state

import com.example.tdm_project.models.Doctor

sealed class DoctorDetailState {
    object Loading : DoctorDetailState()
    data class Success(val doctor: Doctor) : DoctorDetailState()
    data class Error(val message: String) : DoctorDetailState()
}