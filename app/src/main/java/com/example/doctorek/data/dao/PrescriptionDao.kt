package com.example.doctorek.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.doctorek.data.entities.PrescriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescriptionDao {
    @Insert
    suspend fun insert(prescription: PrescriptionEntity): Long

    @Update
    suspend fun update(prescription: PrescriptionEntity)

    @Delete
    suspend fun delete(prescription: PrescriptionEntity)

    @Query("SELECT * FROM prescriptions ORDER BY date DESC")
    fun getAllPrescriptions(): Flow<List<PrescriptionEntity>>

    @Query("SELECT * FROM prescriptions WHERE id = :id")
    suspend fun getPrescriptionById(id: Long): PrescriptionEntity?
}