package com.example.doctorek.data.api


import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.models.DoctorResponse
import com.example.doctorek.data.models.ProfileModel
import com.example.doctorek.data.models.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {
    @PATCH("/rest/v1/profiles")
    suspend fun updateProfile(
        @Body
        profile: ProfileModel,
        @Header("Authorization")
        token : String,
        @Header("apiKey")
        apiKey : String,
        @Query("id")
        id : String
    ): Response<Any>

    @GET("/rest/v1/profiles")
    suspend fun getProfile(
        @Header("Authorization")
        token : String,
        @Header("apiKey")
        apiKey : String,
        @Query("id")
        id : String
    ): Response<List<ProfileResponse>>

    @GET("/rest/v1/doctor_profiles?select=id,specialty,hospital_name,average_rating,profiles:user_id(full_name,avatar_url)&order=average_rating.desc")
    suspend fun getDoctors(
        @Header("Authorization") token: String,
        @Header("apiKey") apiKey: String
    ): Response<List<DoctorResponse>>

    @GET("/rest/v1/doctor_profiles")
    suspend fun getDoctorDetails(
        @Header("Authorization") token: String,
        @Header("apiKey") apiKey: String,
        @Query("id") id: String,
        @Query(value = "select", encoded = true) hh: String = "id,user_id,specialty,hospital_name,hospital_address,location_lat,location_lng,bio,years_of_experience,contact_information,average_rating,created_at,updated_at,doctor_availability!left(day_of_week,is_available),profiles!inner(full_name,avatar_url)",
        @Query(value = "doctor_availability.is_available", encoded = true) filter: String = "eq.true"
    ): Response<List<DoctorDetailResponse>>

}