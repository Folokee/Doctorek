package com.example.doctorek.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.ContactInfo
import com.example.doctorek.data.models.ProfileModel
import com.example.doctorek.data.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val loading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val logoutSuccess: Boolean = false,
    val errorMessage: String? = null,
    val profile: ProfileModel = ProfileModel(),
    val success : Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository(application.applicationContext)
    private val sharedPref = SharedPrefs(application.applicationContext)

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState : StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        if (sharedPref.getAccess() != null){
            getProfile()
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _profileState.update{
                it.copy(
                    loading = true
                )
            }
            val response = repository.getProfile()
            if (response.isSuccess) {
                Log.d("ProfileVM","$response")
                _profileState.update{
                    it.copy(
                        loading = false,
                        profile = response.getOrNull()?.let {
                            ProfileModel(
                                email = it.data.email,
                                phone_number = it.data.phone_number,
                                full_name = it.data.full_name,
                                address = it.data.address,
                                avatar_url = it.data.avatar_url
                            )
                        } ?: ProfileModel(),
                    )
                }
            } else {
                _profileState.update{
                    it.copy(
                        loading = false,
                        errorMessage = response.exceptionOrNull()?.message
                            ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    fun updateProfile(
        phone_number: String,
        full_name: String,
        address: String,
        avatar_url: String
    ) {
        viewModelScope.launch {
            _profileState.update{
                it.copy(
                    loading = true
                )
            }
            val response = repository.updateProfile(
                phone_number,
                full_name,
                address,
                avatar_url
            )
            if (response.isSuccess) {
                val profile = ProfileModel(
                    phone_number = phone_number,
                    full_name = full_name,
                    address = address,
                    avatar_url = avatar_url
                )
                _profileState.update{
                    it.copy(
                        loading = false,
                        profile = profile,
                        success = true
                    )
                }
            } else {
                _profileState.update{
                    it.copy(
                        loading = false,
                        errorMessage = response.exceptionOrNull()?.message
                            ?: "Failed to update profile"
                    )
                }
            }
        }
    }

    fun createDoctorDetails(
        specialty: String,
        hospital_name: String,
        hospital_address: String,
        bio: String,
        years_of_experience: Int,
        contact_information: ContactInfo
    ) {
        viewModelScope.launch {
            _profileState.update{
                it.copy(
                    loading = true
                )
            }
            val response = repository.addDoctorDetails(
                specialty,
                hospital_name,
                hospital_address,
                bio,
                years_of_experience,
                contact_information
            )
            if (response.isSuccess) {
                _profileState.update{
                    it.copy(
                        loading = false,
                        success = true
                    )
                }
            } else {
                _profileState.update{
                    it.copy(
                        loading = false,
                        errorMessage = response.exceptionOrNull()?.message
                            ?: "Failed to update profile"
                    )
                }
            }
        }}

    fun logout(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _profileState.update {
                it.copy(loading = true, isLoggingOut = true)
            }
            
            try {
                // Call logout endpoint if needed
                if (sharedPref.getAccess() != null) {
                    repository.logout()
                }
                
                // Clear all shared preferences
                sharedPref.clearAll()
                
                _profileState.update {
                    it.copy(
                        loading = false,
                        isLoggingOut = false,
                        logoutSuccess = true,
                        profile = ProfileModel(), // Reset profile data
                        success = true
                    )
                }
                onComplete(true)
            } catch (e: Exception) {
                _profileState.update {
                    it.copy(
                        loading = false,
                        isLoggingOut = false,
                        errorMessage = "Failed to logout: ${e.message}"
                    )
                }
                onComplete(false)
            }
        }
    }

    fun resetSuccess(){
        _profileState.update{
            it.copy(
                success = false
            )
        }
    }

}