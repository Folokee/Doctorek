package com.example.doctorek.data.auth

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue


object SupabaseClient {
    val client by lazy {
        createSupabaseClient(
            supabaseUrl = "https://epcuwqfdzmbungpeyhuk.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVwY3V3cWZkem1idW5ncGV5aHVrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI1MTczNjUsImV4cCI6MjA1ODA5MzM2NX0.7O4GKeSugQE8HgHlKYrjOjSBM33MKP_HpbJ8k2xK0lw"
        ) {
            install(GoTrue)
        }
    }
}