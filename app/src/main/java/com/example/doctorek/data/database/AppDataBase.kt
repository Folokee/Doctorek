package com.example.doctorek.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.doctorek.data.dao.PatientDao
import com.example.doctorek.data.dao.PrescriptionDao
import com.example.doctorek.data.entities.PatientEntity
import com.example.doctorek.data.entities.PrescriptionEntity
import com.example.doctorek.ui.screens.doctorScreens.Medication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(entities = [PrescriptionEntity::class, PatientEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prescriptionDao(): PrescriptionDao
    abstract fun patientDao(): PatientDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "doctorek_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromMedicationList(value: List<Medication>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toMedicationList(value: String): List<Medication> {
        val listType = object : TypeToken<List<Medication>>() {}.type
        return Gson().fromJson(value, listType)
    }
}