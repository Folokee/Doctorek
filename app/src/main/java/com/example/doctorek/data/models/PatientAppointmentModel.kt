package com.example.doctorek.data.models

data class PatientAppointment(
    val id: String,
    val patient: String,
    val doctor: String,
    val patient_id: String,
    val doctor_id: String,
    val appointment_date: String,
    val start_time: String,
    val end_time: String,
    val status: String,
    val reason: String?,
    val notes: String?,
    val qr_code: String?,
    val price: Double? = null, // Added price field with default null
    val created_at: String,
    val updated_at: String,
    val doctor_info: DoctorInfo? = null
) {
    // Helper property to format the appointment time (for display)
    val appointment_time: String
        get() = start_time.split(":").take(2).joinToString(":")
    
    // Helper property to get normalized status
    val normalizedStatus: String
        get() = status.lowercase()
}

// Update to match the doctor_info structure from the API
data class DoctorInfo(
    val full_name: String?,
    val speciality: String?,
    val hospital_name: String?,
    val avatar_url: String?
)

// Keep for backwards compatibility with existing code
typealias DoctorDetails = DoctorInfo 

data class PatientInfo(
    val full_name: String,
    val phone_number: String,
    val age_range: String,
    val gender: String,
    val problem: String
)
