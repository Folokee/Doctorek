package com.example.doctorek.services

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.doctorek.R
import com.example.doctorek.data.api.ApiConfig
import com.example.doctorek.data.auth.SharedPrefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class FCMService : FirebaseMessagingService() {
    
    private lateinit var sharedPrefs: SharedPrefs
    
    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPrefs(applicationContext)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "New token received: $token")
        
        // Save token in SharedPreferences
        sharedPrefs.save("fcm_token", token)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sendDeviceTokenToSupabase(token)
                Log.d("FCMService", "FCM token sent successfully to Supabase")
            } catch (e: Exception) {
                Log.e("FCMService", "Error sending FCM token to Supabase", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCMService", "Message received: $remoteMessage")

        remoteMessage.notification?.let {
            val title = it.title ?: "New Notification"
            val body = it.body ?: ""
            showNotification(title, body)
        }

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCMService", "Data payload: ${remoteMessage.data}")
            val title = remoteMessage.data["title"] ?: "New Notification"
            val body = remoteMessage.data["body"] ?: remoteMessage.data["message"] ?: ""
            showNotification(title, body)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(title: String, message: String) {
        val channelId = "default_channel_id"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

        val channel = android.app.NotificationChannel(
            channelId,
            "Default Channel",
            android.app.NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val builder = android.app.Notification.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun sendDeviceTokenToSupabase(token: String) {
        val userId = sharedPrefs.getUserId()

        if (userId.isNullOrEmpty()) {
            Log.w("FCMService", "User ID is null or empty, cannot send device token.")
            return
        }

        val client = OkHttpClient()
        val json = """
            {
                "fcm_token": "$token"
            }
        """.trimIndent()
        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${ApiConfig.BASE_URL}/rest/v1/profiles?id=eq.$userId")
            .patch(body)
            .addHeader("apikey", ApiConfig.S_API_KEY)
            .addHeader("Authorization", "Bearer ${ApiConfig.S_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=minimal")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e("FCMService", "Failed to send device token: ${response.code}")
                throw Exception("Failed to send device token: ${response.code}")
            } else {
                Log.d("FCMService", "Device token successfully sent to Supabase")
            }
        }
    }
}