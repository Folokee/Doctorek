package com.example.doctorek.data.models

import com.example.doctorek.ui.screens.doctorScreens.Medication
import java.time.LocalDate


data class PrescriptionModel(
    val id: String,
    val local_id: Long = 0,
    val appointment_id : String,
    val patient_id : String,
    val doctor_id : String,
    val prescription_date : String,
    val details : List<Medication> ,
    val additional_notes : String,
    val pdf_url : String,
    val is_synced : Boolean = false,
    val patient : PatientInfo
) {
    fun getPrescriptionDate(): LocalDate {
        return LocalDate.parse(prescription_date)
    }
}

data class PatientInfo(
    val id: String,
    val name : String,
)

data class SavePrescriptionModel(
    val appointment_id : String,
    val patient_id : String,
    val details : List<Medication> ,
    val additional_notes : String,
)