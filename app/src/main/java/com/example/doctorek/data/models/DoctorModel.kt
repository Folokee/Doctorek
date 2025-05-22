package com.example.doctorek.data.models

data class DoctorProfileInfo(
    val full_name: String?,
    val avatar_url: String?
)

data class DoctorResponse(
    val id: String,
    val specialty: String,
    val hospital_name: String,
    val average_rating: Double,
    val profiles: DoctorProfileInfo
)

data class DoctorDetailResponse(
    val id: String,
    val user_id: String,
    val specialty: String,
    val hospital_name: String,
    val hospital_address: String,
    val location_lat: Double,
    val location_lng: Double,
    val bio: String,
    val years_of_experience: Int,
    val contact_information: ContactInformation,
    val average_rating: Double,
    val profiles: DoctorProfileInfo,
    val created_at: String,
    val updated_at: String,
    val availability: List<DoctorAvailability> = emptyList() // Changed from doctor_availability to availability
)

data class ContactInformation(
    val email: String? = null,
    val phone: String? = null,
    val office_hours: String? = null,
    val facebook_link: String? = null,
    val linkedin_link: String? = null,
    val whatsapp_link: String? = null
)

data class DoctorAvailability(
    val day_of_week: String,
    val start_time: String,
    val end_time: String,
    val slot_duration: Int,
    val is_available: Boolean
)
