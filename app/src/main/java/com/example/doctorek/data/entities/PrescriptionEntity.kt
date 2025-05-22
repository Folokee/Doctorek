package com.example.doctorek.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.doctorek.ui.screens.doctorScreens.Medication
import java.time.LocalDate

@Entity(tableName = "prescriptions")
data class PrescriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val local_id: Long = 0,
    val appointment_id : String? = null,
    val patient_id : String,
    val details : List<Medication>,
    val additional_notes : String,
    var is_synced : Boolean = false,
)
