package com.example.doctorek.ui.screens.preview

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.doctorek.data.models.ContactInformation
import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.models.DoctorProfileInfo
import com.example.doctorek.repositories.DoctorRepository
import com.example.doctorek.ui.screens.state.DoctorDetailState
import com.example.doctorek.ui.viewmodels.DoctorDetailViewModel

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
    private fun createMockDoctor(): DoctorDetailResponse {
        return DoctorDetailResponse(
            id = "mock_id",
            user_id = "user_123",
            specialty = "Virologist",
            hospital_name = "Clinic El Hanaad",
            hospital_address = "City Zaater Ettayeb",
            location_lat = 36.7538,
            location_lng = 3.0588,
            bio = "Dr. Bellamy Nicholas is a top specialist at London Bridge Hospital at London. He has achieved several awards and recognition for his contribution and service in his own field. He is available for private consultation.",
            years_of_experience = 10,
            contact_information = ContactInformation(
                email = "dr.seddik@example.com",
                phone = "+213 55 12 34 567",
                office_hours = "Mon - Sat (08:30 AM - 09:00 PM)",
                facebook_link = "https://facebook.com/dr.seddik",
                linkedin_link = "https://linkedin.com/in/dr-seddik",
                whatsapp_link = "https://wa.me/213551234567"
            ),
            average_rating = 4.5,
            profiles = DoctorProfileInfo(
                full_name = "Dr. seddik walid",
                avatar_url = null
            ),
            created_at = "2023-01-01T00:00:00Z",
            updated_at = "2023-05-15T00:00:00Z"
        )
    }
}