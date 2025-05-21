package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.models.DoctorResponse
import com.example.doctorek.data.repositories.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DoctorListState(
    val doctors: List<DoctorResponse> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

data class DoctorDetailState(
    val doctor: DoctorDetailResponse? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class DoctorViewModel(application: Application) : AndroidViewModel(application) {
    private val doctorRepository = DoctorRepository(application.applicationContext)

    // Doctor list state
    private val _doctorListState = MutableStateFlow(DoctorListState())
    val doctorListState: StateFlow<DoctorListState> = _doctorListState

    // Doctor detail state
    private val _doctorDetailState = MutableStateFlow(DoctorDetailState())
    val doctorDetailState: StateFlow<DoctorDetailState> = _doctorDetailState

    // Unique specialties for filtering
    val uniqueSpecialties: StateFlow<List<String>> = doctorListState.map { state ->
        state.doctors.map { it.specialty }.distinct().sorted()
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    init {
        fetchDoctors()
    }

    fun fetchDoctors() {
        viewModelScope.launch {
            _doctorListState.update { it.copy(loading = true, error = null) }

            doctorRepository.getDoctors().collect { result ->
                result.fold(
                    onSuccess = { doctors ->
                        _doctorListState.update {
                            it.copy(doctors = doctors, loading = false, error = null)
                        }
                    },
                    onFailure = { e ->
                        _doctorListState.update {
                            it.copy(loading = false, error = e.message ?: "Unknown error")
                        }
                    }
                )
            }
        }
    }

    fun fetchDoctorDetails(doctorId: String) {
        viewModelScope.launch {
            _doctorDetailState.update { it.copy(loading = true, error = null) }

            doctorRepository.getDoctorDetails(doctorId).collect { result ->
                result.fold(
                    onSuccess = { doctor ->
                        _doctorDetailState.update {
                            it.copy(doctor = doctor, loading = false, error = null)
                        }
                    },
                    onFailure = { e ->
                        _doctorDetailState.update {
                            it.copy(loading = false, error = e.message ?: "Unknown error")
                        }
                    }
                )
            }
        }
    }

    fun clearDoctorDetail() {
        _doctorDetailState.update { DoctorDetailState() }
    }
}