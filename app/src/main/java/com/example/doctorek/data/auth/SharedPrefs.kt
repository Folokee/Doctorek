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

    fun setFirstTime(value: Boolean) {
        sharedPreferences.edit() { putBoolean("onboarding", value) }
    }

    fun getFirstTime(): Boolean {
        return sharedPreferences.getBoolean("onboarding", true)
    }

    fun saveUserId(value: String) {
        sharedPreferences.edit() { putString("user_id", value) }
    }
    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun save(key : String, value : String){
        sharedPreferences.edit() { putString(key, value) }
    }

    fun save(key : String, value : Int){
        sharedPreferences.edit() { putInt(key, value) }
    }
    fun getString(key : String) : String? {
        return sharedPreferences.getString(key, null)
    }
    fun getInt(key : String) : Int? {
        return sharedPreferences.getInt(key, -1)
    }

    fun clearAll() {
        sharedPreferences.edit() { clear() }
    }
}