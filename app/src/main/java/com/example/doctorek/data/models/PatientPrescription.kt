package com.example.doctorek.data.models

import com.google.gson.annotations.SerializedName

data class PatientPrescription(
    val id: String,
    val doctor: PrescriptionDoctor,
    val patient: PrescriptionPatient,
    @SerializedName("prescription_date") val prescriptionDate: String,
    val details: PrescriptionDetails,  // Changed from List<Medication> to PrescriptionDetails
    @SerializedName("additional_notes") val additionalNotes: String?,
    @SerializedName("appointment_id") val appointmentId: String?,
    @SerializedName("appointment_date") val appointmentDate: String?,
    @SerializedName("pdf_url") val pdfUrl: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("is_synced") val isSynced: Boolean,
    @SerializedName("local_id") val localId: String
)

data class PrescriptionDoctor(
    val id: String,
    val name: String,
    val specialty: String,
    @SerializedName("hospital_name") val hospitalName: String,
    @SerializedName("avatar_url") val avatarUrl: String
)

data class PrescriptionPatient(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)

// This class represents the structure of the 'details' object
data class PrescriptionDetails(
    val medications: List<Medication>
)

data class Medication(
    val name: String,
    val dosage: String,
    val duration: String,
    val frequency: String
)
