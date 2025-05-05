package com.example.doctorek.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "prescriptions")
data class PrescriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val local_id: Long = 0,
    val patient_id : Long,
    val doctor_id : Long,
    val prescription_date : LocalDate,
    val details : String,
    val additional_notes : String,
    val pdf_url : String,
)
