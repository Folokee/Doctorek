package com.example.doctorek.ui.screens.preview


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.DoctorModel
import com.example.doctorek.data.models.Hospital
import com.example.doctorek.repositories.DoctorRepository
import com.example.doctorek.ui.screens.state.DoctorDetailState
import com.example.doctorek.ui.viewmodels.DoctorDetailViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class MockDoctorDetailViewModel : DoctorDetailViewModel(DoctorRepository()) {
    init {
        // Pre-populate the state with mock data
        _state.value = DoctorDetailState.Success(createMockDoctor())
        _favoriteState.value = false
        _selectedDayIndex.value = 0
    }

    // Override to prevent actual API calls
    override fun loadDoctorDetails(doctorId: String) {
        // Do nothing - mock data is already loaded
    }

    // Toggle favorite for the preview
    override fun toggleFavorite() {
        _favoriteState.value = !_favoriteState.value
    }

    // Create mock doctor data
    private fun createMockDoctor(): DoctorModel {
        return DoctorModel(
            id = "mock_id",
            name = "Dr. seddik walid",
            specialty = "Virologist",
            hospital = Hospital(
                mapLocation = "hospital_1",
                name = "Clinic El Hanaad",
                address = "City Zaater Ettayeb"
            ),
            phoneNumber = "+213 55 12 34 567",
            email = "dr.seddik@example.com",
            about = "Dr. Bellamy Nicholas is a top specialist at London Bridge Hospital at London. He has achieved several awards and recognition for is contribution and service in his own field. He is available for private consultation.",
            experience = "10 Yrs",
            rating = "2.3",
            patientsCount = "1000+",
            workingHours = "Mon - Sat (08:30 AM - 09:00 PM)",
            profileImage = "null"  // No image URL for preview
        )
    }
}