package com.example.doctorek.data.api

import com.example.doctorek.data.models.DoctorModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DoctorApiService {
    @GET("doctors/{doctorId}")
    suspend fun getDoctorById(@Path("doctorId") doctorId: String): Response<DoctorModel>
}