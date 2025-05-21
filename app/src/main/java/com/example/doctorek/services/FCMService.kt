package com.example.doctorek.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.doctorek.R
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.utils.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob

class FCMService : FirebaseMessagingService() {
    
    private val TAG = "FCMService"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var sharedPrefs: SharedPrefs
    
    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPrefs(applicationContext)
        
        // Try to update token on service creation to ensure it's registered
        serviceScope.launch {
            try {
                val token = sharedPrefs.getString("fcm_token")
                if (!token.isNullOrEmpty()) {
                    Log.d(TAG, "Attempting to update token on service creation")
                    NotificationUtils.forceTokenUpdate(sharedPrefs)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating token on service creation", e)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token received: $token")
        
        // Store locally first
        sharedPrefs.save("fcm_token", token)
        sharedPrefs.save("token_sync_success", "false") // Reset sync flag
        
        // Try to update Supabase
        serviceScope.launch {
            try {
                val success = NotificationUtils.updateFcmToken(token, sharedPrefs)
                Log.d(TAG, "FCM token update attempt result: $success")
                
                if (!success) {
                    // Schedule a retry
                    Log.d(TAG, "Scheduling token update retry...")
                    // This is a simple retry - in a real app, you might use WorkManager
                    kotlinx.coroutines.delay(10000) // Wait 10 seconds
                    val retrySuccess = NotificationUtils.updateFcmToken(token, sharedPrefs)
                    Log.d(TAG, "FCM token update retry result: $retrySuccess")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token to Supabase", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received: $remoteMessage")
        
        // Check for token sync status and update if needed
        serviceScope.launch {
            val syncSuccess = sharedPrefs.getString("token_sync_success")
            if (syncSuccess != "true") {
                Log.d(TAG, "Token sync not successful, trying to update on message received")
                NotificationUtils.forceTokenUpdate(sharedPrefs)
            }
        }
        
        // Handle notification payload
        remoteMessage.notification?.let {
            val title = it.title ?: "New Notification"
            val body = it.body ?: ""
            showNotification(title, body)
        }
        
        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data payload: ${remoteMessage.data}")
            val title = remoteMessage.data["title"] ?: "New Notification"
            val body = remoteMessage.data["body"] ?: remoteMessage.data["message"] ?: ""
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Use unique ID for each notification
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}