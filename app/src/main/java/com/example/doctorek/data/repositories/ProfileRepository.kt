package com.example.doctorek.data.repositories

import android.content.Context
import android.util.Log
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.api.ApiConfig
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.ContactInfo
import com.example.doctorek.data.models.DoctorProfileModel
import com.example.doctorek.data.models.ProfileModel
import com.example.doctorek.data.models.ProfileResponse
import com.example.doctorek.data.models.UpdateProfileModel

class ProfileRepository(private val context : Context) {
    private val apiService = ApiClient.apiService
    private val sharedPrefs = SharedPrefs(context)

    suspend fun getProfile() : Result<ProfileResponse>{
        val token = sharedPrefs.getAccess()
        val apiKey = ApiConfig.S_API_KEY
        val userId = "eq.${sharedPrefs.getString("user_id")}"

        return try {
            val response = apiService.getProfile("Bearer $token")
            Log.d("ProfileRepository", "Response: ${response.body()}")
            if(response.isSuccessful && response.body() != null && response.body() != null){
                Result.success(response.body()!!)
            } else {
                val errorResponse = response.errorBody()?.string() ?: "No profile found"
                Result.failure(Exception(errorResponse))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }

    }

    suspend fun updateProfile(
        phone_number: String,
        full_name: String,
        address: String,
        avatar_url: String
    ): Result<String> {
        val token = sharedPrefs.getAccess()
        val updatedProfile = UpdateProfileModel(
            phone_number = phone_number,
            full_name = full_name,
            address = address,
            avatar_url = avatar_url
        )
        return try {
            val response = apiService.updateProfile(
                updatedProfile,
                "Bearer $token"
            )
            if(response.isSuccessful){
                sharedPrefs.saveName(full_name)
                Result.success("Okay")
            } else {
                val errorResponse = response.errorBody()?.string()
                Result.failure(Exception(errorResponse))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }
    }

    suspend fun addDoctorDetails(
        specialty: String,
        hospital_name: String,
        hospital_address: String,
        bio: String,
        years_of_experience: Int,
        contact_information: ContactInfo
    ): Result<String> {
        val token = sharedPrefs.getAccess()
        val updatedProfile = DoctorProfileModel(
            specialty,
            hospital_name,
            hospital_address,
            bio,
            years_of_experience,
            contact_information
        )
        return try {
            val response = apiService.createDoctorProfile(
                "Bearer $token",
                updatedProfile,
            )
            if(response.isSuccessful){
                Result.success("Okay")
            } else {
                val errorResponse = response.errorBody()?.string()
                Result.failure(Exception(errorResponse))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }
    }

    suspend fun logout(): Result<String> {
        val token = sharedPrefs.getAccess()
        
        return try {
            if (token != null) {
                val response = apiService.logout("Bearer $token")
                if (response.isSuccessful) {
                    Result.success("Logged out successfully")
                } else {
                    // Even if the API call fails, we'll still clear local data
                    Result.success("Logged out locally")
                }
            } else {
                Result.success("No active session")
            }
        } catch (e: Exception) {
            // Even if the API call throws an exception, we'll still clear local data
            Result.success("Logged out locally: ${e.message}")
        }
    }

}