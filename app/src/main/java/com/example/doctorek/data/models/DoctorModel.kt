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
    val doctor_availability: List<DoctorAvailability> = emptyList() // Added doctor availability
)

data class ContactInformation(
    val email: String,
    val phone: String,
    val office_hours: String,
    val facebook_link: String,
    val linkedin_link: String,
    val whatsapp_link: String
)
