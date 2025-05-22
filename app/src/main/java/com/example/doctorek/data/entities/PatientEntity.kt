package com.example.doctorek.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val local_id: Long = 0,
    val patient_id : String,
    val full_name : String,
    val email : String,
)
