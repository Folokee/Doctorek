package com.example.doctorek.data.api


import com.example.doctorek.data.models.AppointmentRequest
import com.example.doctorek.data.models.AppointmentResponse
import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.models.DoctorResponse
import com.example.doctorek.data.models.ProfileModel
import com.example.doctorek.data.models.ProfileResponse
import com.example.doctorek.data.models.ResponseModel
import com.example.doctorek.data.models.SigninRequest
import com.example.doctorek.data.models.SigninResponse
import com.example.doctorek.data.models.SignupRequest
import com.example.doctorek.data.models.SignupResponse
import com.example.doctorek.data.models.PatientAppointment
import com.example.doctorek.data.models.DoctorAvailabilityResponse
import com.example.doctorek.data.models.PatientPrescription
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @PATCH("/api/profile/")
    suspend fun updateProfile(
        @Body
        profile: ProfileModel,
        @Header("Authorization")
        token : String,
    ): Response<ResponseModel>

    @GET("/api/profile/")
    suspend fun getProfile(
        @Header("Authorization")
        token : String,
    ): Response<ProfileResponse>

    @GET("api/doctors/")
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

    @POST("/api/signup/")
    suspend fun signUp(
        @Body
        request : SignupRequest
    ) : Response<SignupResponse>

    @POST("/api/login/")
    suspend fun signIn(
        @Body
        request : SigninRequest
    ) : Response<SigninResponse>

    @POST("/api/signout/")
    suspend fun logout(
        @Header("Authorization")
        token : String,
    ) : Response<Any>


    @GET("/api/doctors/")
    suspend fun getDoctorById(@Query("id") doctorId: String,
    @Header("Authorization") token: String,
    ): Response<DoctorDetailResponse>

    @GET("/api/doctors/{doctorId}/appointments/available")
    suspend fun getAvailableSlots(
        @Path("doctorId") doctorId: String,
        @Query("date") date: String
    ): Response<Map<String, List<String>>>

    @POST("/api/appointments")
    suspend fun bookAppointment(
        @Body appointmentRequest: AppointmentRequest
    ): Response<AppointmentResponse>

    @POST("/api/appointments/")
    suspend fun createAppointment(
        @Body appointmentRequest: AppointmentRequest,
        @Header("Authorization") token: String
    ): Response<AppointmentResponse>

    @GET("/api/appointments/patient_appointments/")
    suspend fun getPatientAppointments(
        @Query("patient_id") patientId: String,
        @Header("Authorization") token: String
    ): Response<List<PatientAppointment>>

    @GET("/api/doctor-availability/")
    suspend fun getDoctorAvailability(
        @Query("doctor_id") doctorId: String,
        @Query("date") date: String,
        @Header("Authorization") token: String
    ): Response<DoctorAvailabilityResponse>

    @GET("/api/prescriptions/patient_prescriptions/")
    suspend fun getPatientPrescriptions(
        @Query("patient_id") patientId: String,
        @Header("Authorization") token: String
    ): Response<List<PatientPrescription>>
}