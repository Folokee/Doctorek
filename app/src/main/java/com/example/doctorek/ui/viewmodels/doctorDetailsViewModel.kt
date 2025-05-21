package com.example.doctorek.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.repositories.DoctorRepository
import com.example.doctorek.ui.screens.state.DoctorDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class DoctorDetailViewModel(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    val _state = MutableStateFlow<DoctorDetailState>(DoctorDetailState.Loading)
    val state: StateFlow<DoctorDetailState> = _state

    val _favoriteState = MutableStateFlow(false)
    val favoriteState: StateFlow<Boolean> = _favoriteState

    val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex

    open fun loadDoctorDetails(doctorId: String) {
        _state.value = DoctorDetailState.Loading

        viewModelScope.launch {
            doctorRepository.getDoctorById(doctorId)
                .onSuccess { doctor ->
                    _state.value = DoctorDetailState.Success(doctor)
                }
                .onFailure { error ->
                    _state.value = DoctorDetailState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }

    open fun toggleFavorite() {
        _favoriteState.value = !_favoriteState.value
        // In a real app, you would save this preference to a repository
    }

    fun selectDay(index: Int) {
        _selectedDayIndex.value = index
    }

    // Factory class to create the ViewModel with dependencies
    class Factory(private val doctorRepository: DoctorRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DoctorDetailViewModel::class.java)) {
                return DoctorDetailViewModel(doctorRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}