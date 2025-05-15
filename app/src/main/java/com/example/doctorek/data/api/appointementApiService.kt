package com.example.doctorek.data.api

import com.example.doctorek.data.models.AppointmentRequest
import com.example.doctorek.data.models.AppointmentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface AppointmentApiService {
    @GET("doctors/{doctorId}/appointments/available")
    suspend fun getAvailableSlots(
        @Path("doctorId") doctorId: String,
        @Query("date") date: String
    ): Response<Map<String, List<String>>>

    @POST("appointments")
    suspend fun bookAppointment(
        @Body appointmentRequest: AppointmentRequest
    ): Response<AppointmentResponse>
}