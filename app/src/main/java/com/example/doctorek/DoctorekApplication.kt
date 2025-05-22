package com.example.doctorek

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class DoctorekApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            Log.d("DoctorekApplication", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("DoctorekApplication", "Error initializing Firebase", e)
        }
    }
}
