package com.example.doctorek.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.doctorek.data.entities.PatientEntity

@Dao
interface PatientDao {
    @Insert
    suspend fun insert(patient: PatientEntity): Long

    @Update
    suspend fun update(patient: PatientEntity)

    @Delete
    suspend fun delete(patient: PatientEntity)

    //create a function to insert a list of entities
    @Insert
    suspend fun insertAll(patients: List<PatientEntity>): List<Long>

    @Query("SELECT * FROM PatientEntity where patient_id = :patientId")
    suspend fun getPatientById(patientId: String): PatientEntity?

    @Query("DELETE FROM PatientEntity")
    suspend fun deleteAll()
}