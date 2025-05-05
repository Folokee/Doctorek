package com.example.doctorek.data.repositories

import android.content.Context
import androidx.core.content.edit
import com.example.doctorek.data.api.ApiClient
import com.example.doctorek.data.auth.SupabaseClient.client
import com.google.gson.Gson
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository(private val context : Context) {

    private val apiService = ApiClient.apiService
    private val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)


    suspend fun signup(userEmail: String, userPassword: String, role: String){
        client.auth.signUpWith(Email){
            email = userEmail
            password = userPassword
            data = JsonObject(mapOf(
                "user_type" to JsonPrimitive(role)
            ))
        }
        saveToken()
    }





    private fun saveToken() {
        val session = client.auth.currentSessionOrNull()
        val accessToken = session?.accessToken
        sharedPreferences.edit() { putString("token", accessToken) }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun clearToken() {
        sharedPreferences.edit() { remove("token") }
    }

}