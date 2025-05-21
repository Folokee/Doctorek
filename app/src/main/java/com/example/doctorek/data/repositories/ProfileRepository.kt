package com.example.doctorek.data.repositories

import android.content.Context
import android.util.Log
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.api.ApiConfig
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.ProfileModel
import com.example.doctorek.data.models.ProfileResponse

class ProfileRepository(private val context : Context) {
    private val apiService = ApiClient.apiService
    private val sharedPrefs = SharedPrefs(context)

    suspend fun getProfile() : Result<ProfileResponse>{
        val token = sharedPrefs.getAccess()
        val apiKey = ApiConfig.S_API_KEY
        val userId = "eq.${sharedPrefs.getString("user_id")}"

        return try {
            val response = apiService.getProfile("Bearer $token", apiKey, userId)
            Log.d("ProfileRepository", "Response: ${response.body()}")
            if(response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()){
                Result.success(response.body()!!.first())
            } else {
                val errorResponse = response.errorBody()?.string() ?: "No profile found"
                Result.failure(Exception(errorResponse))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message))
        }

    }

    suspend fun updateProfile(
        email: String,
        phone_number: String,
        full_name: String,
        address: String,
        avatar_url: String
    ): Result<String> {
        val token = sharedPrefs.getAccess()
        val apiKey = ApiConfig.S_API_KEY
        val updatedProfile = ProfileModel(
            email,
            phone_number,
            full_name,
            address,
            avatar_url
        )
        val userId = "eq.${sharedPrefs.getString("user_id")}"

        return try {
            val response = apiService.updateProfile(
                updatedProfile,
                "Bearer $token",
                apiKey,
                userId
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

}