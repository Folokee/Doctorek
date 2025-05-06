package com.example.doctorek.data.auth

import android.content.Context
import androidx.core.content.edit

class SharedPrefs(private val context: Context){
    private val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveAccess(value: String) {
        sharedPreferences.edit() { putString("access_token", value) }
    }

    fun getAccess(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun saveRefresh(value: String) {
        sharedPreferences.edit() { putString("refresh_token", value) }
    }

    fun getRefresh(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun setFirstTime(value: Boolean) {
        sharedPreferences.edit() { putBoolean("onboarding", value) }
    }

    fun getFirstTime(): Boolean {
        return sharedPreferences.getBoolean("onboarding", true)
    }

    fun clearAll() {
        sharedPreferences.edit() { clear() }
    }
}