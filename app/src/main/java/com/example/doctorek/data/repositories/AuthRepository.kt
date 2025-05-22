package com.example.doctorek.data.repositories

import android.content.Context
import android.util.Log
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.models.SigninRequest
import com.example.doctorek.data.models.SigninResponse
import com.example.doctorek.data.models.SignupRequest
import com.example.doctorek.data.models.SignupResponse

sealed class Role(
    val role: String
) {
    object Doctor : Role("doctor")
    object Patient : Role("patient")
}

class AuthRepository(private val context : Context) {
    private val sharedPrefs = SharedPrefs(context)
    private val apiService = ApiClient.apiService

    suspend fun signIn(email: String, password: String) : Result<SigninResponse>{
        return try {
            val request = SigninRequest(
                email = email,
                password = password
            )
            val response = apiService.signIn(request)
            if(response.isSuccessful && response.body()!!.success){
                val body = response.body()!!
                //Necessary saves in shared preferences
                sharedPrefs.saveAccess(body.data.access_token)
                sharedPrefs.saveUserId(body.data.userId)
                sharedPrefs.save("user_email", email)
                sharedPrefs.saveType(body.data.user_type?: "not found")
                Result.success(body)
            } else {
                val errorResponse = response.body()!!.message
                Result.failure(Exception(errorResponse))
            }
        } catch (e : Exception){
            Result.failure(Exception(e.message))
        }


    }

    suspend fun signUp(email : String, password : String, role : String): Result<SignupResponse>{
        return try {
            val request = SignupRequest(
                email = email,
                password = password,
                user_type = role
            )
            val response = apiService.signUp(request)
            if(response.isSuccessful && response.body()!!.success){
                val body = response.body()!!
                //Necessary saves in shared preferences
                sharedPrefs.saveAccess(body.data.access_token)
                sharedPrefs.saveUserId(body.data.userId)
                sharedPrefs.save("user_email", email)
                sharedPrefs.saveType(role)
                Result.success(body)
            } else {
                val errorResponse = response.body()!!.message
                Result.failure(Exception(errorResponse))

            }
        }catch (e : Exception){
            Result.failure(Exception(e.message))
        }
    }

    suspend fun logout() : Result<Any> {
        return try {
            val token = sharedPrefs.getAccess()
            val response = apiService.logout("Bearer $token")
            if (response.isSuccessful && response.body() != null){
                val body = response.body()!!
                sharedPrefs.clearAll()
                Result.success(body)
            }else {
                val errorResponse = response.errorBody()?.string() ?: "Signup failed"
                Result.failure(Exception(errorResponse))
            }
        } catch(e: Exception){
            Result.failure(Exception(e.message))
        }
    }

}