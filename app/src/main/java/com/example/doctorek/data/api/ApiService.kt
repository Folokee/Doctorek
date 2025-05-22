package com.example.doctorek.data.api


import com.example.doctorek.data.models.AppointmentModel
import com.example.doctorek.data.models.AppointmentRequest
import com.example.doctorek.data.models.AppointmentResponse
import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.models.DoctorProfileModel
import com.example.doctorek.data.models.DoctorResponse
import com.example.doctorek.data.models.PatientModel
import com.example.doctorek.data.models.PrescriptionModel
import com.example.doctorek.data.models.ProfileModel
import com.example.doctorek.data.models.ProfileResponse
import com.example.doctorek.data.models.ResponseModel
import com.example.doctorek.data.models.SavePrescriptionModel
import com.example.doctorek.data.models.SigninRequest
import com.example.doctorek.data.models.SigninResponse
import com.example.doctorek.data.models.SignupRequest
import com.example.doctorek.data.models.SignupResponse
import com.example.doctorek.data.models.UpdateAppStatus
import com.example.doctorek.data.models.UpdateProfileModel
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
        profile: UpdateProfileModel,
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

    @POST("/api/doctors/")
    suspend fun createDoctorProfile(
        @Header("Authorization")
        token : String,
        @Body
        doctorProfile: DoctorProfileModel
    ) : Response<Any>

    @GET("/api/doctor-appointments/")
    suspend fun getDoctorAppointments(
        @Header("Authorization")
        token : String,
    ) : Response<List<AppointmentModel>>

    @PATCH("/api/doctor-appointments/")
    suspend fun updateAppointmentStatus(
        @Header("Authorization")
        token : String,
        @Body
        newStatus : UpdateAppStatus
    ) : Response<Any>

    @GET("/api/doctor/prescriptions/")
    suspend fun getPrescriptions(
        @Header("Authorization")
        token : String
    ) : Response<List<PrescriptionModel>>

    @POST("/api/prescriptions/")
    suspend fun createPrescription(
        @Header("Authorization")
        token : String,
        @Body
        prescription: SavePrescriptionModel
    ) : Response<Any>

    @GET("/api/patients/")
    suspend fun getPatients(
        @Header("Authorization")
        token : String,
    ) : Response<List<PatientModel>>


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