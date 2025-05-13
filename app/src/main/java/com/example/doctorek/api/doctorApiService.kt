package com.example.tdm_project.api

import com.example.tdm_project.models.Doctor
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DoctorApiService {
    @GET("doctors/{doctorId}")
    suspend fun getDoctorById(@Path("doctorId") doctorId: String): Response<Doctor>
}