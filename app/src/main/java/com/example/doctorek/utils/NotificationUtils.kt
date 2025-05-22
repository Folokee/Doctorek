package com.example.doctorek.utils

import android.util.Log
import com.example.doctorek.data.api.ApiConfig
import com.example.doctorek.data.auth.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object NotificationUtils {
    
    private const val TAG = "NotificationUtils"
    
    suspend fun updateFcmToken(token: String, sharedPrefs: SharedPrefs): Boolean = withContext(Dispatchers.IO) {
        val userId = sharedPrefs.getUserId()
        Log.d(TAG, "Attempting to update FCM token for user: $userId")

        if (userId.isNullOrEmpty()) {
            Log.w(TAG, "User ID is null or empty, cannot send device token.")
            return@withContext false
        }

        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
                
            val json = """
                {
                    "fcm_token": "$token"
                }
            """.trimIndent()
            Log.d(TAG, "Preparing request with token: $token")
            
            val body = json.toRequestBody("application/json".toMediaType())
            val url = "${ApiConfig.SUPABASE_URL}/rest/v1/profiles?id=eq.$userId"
            Log.d(TAG, "Sending request to URL: $url")

            val request = Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("apikey", ApiConfig.S_API_KEY)
                .addHeader("Authorization", "Bearer ${ApiConfig.S_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build()

            client.newCall(request).execute().use { response ->
                val responseCode = response.code
                val isSuccessful = response.isSuccessful
                val responseBody = response.body?.string() ?: "Empty response"
                
                Log.d(TAG, "Response code: $responseCode, Success: $isSuccessful")
                Log.d(TAG, "Response body: $responseBody")
                
                if (!isSuccessful) {
                    Log.e(TAG, "Failed to send device token: $responseCode - $responseBody")
                    return@withContext false
                } else {
                    Log.d(TAG, "Device token successfully sent to Supabase")
                    // Save a flag indicating successful token update
                    sharedPrefs.save("token_sync_success", "true")
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception sending token to Supabase", e)
            return@withContext false
        }
    }
    
    // Force an immediate token update regardless of any conditions
    suspend fun forceTokenUpdate(sharedPrefs: SharedPrefs): Boolean {
        try {
            val token = sharedPrefs.getString("fcm_token")
            if (token.isNullOrEmpty()) {
                Log.w(TAG, "No token available for forced update")
                return false
            }
            
            Log.d(TAG, "Forcing token update to Supabase")
            return updateFcmToken(token, sharedPrefs)
        } catch (e: Exception) {
            Log.e(TAG, "Error in forced token update", e)
            return false
        }
    }
}
