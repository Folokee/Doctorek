package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.auth.SupabaseClient.client

import kotlinx.coroutines.launch


import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

data class UserState(
    val Loading: Boolean = false,
    val errorMessage: String? = null,
    val isSignedIn: Boolean = false,
)


class AuthViewModel (application: Application) : AndroidViewModel(application) {
    private val _userState = mutableStateOf<UserState>(UserState())
    val userState: State<UserState> = _userState
    private val sharedPref = SharedPrefs(application.applicationContext)

    fun signUp(
        userEmail: String,
        userPassword: String,
        role : String
    ) {
        viewModelScope.launch {
            try {
                _userState.value = UserState(
                    Loading = true
                )

                client.auth.signUpWith(Email){
                    email = userEmail
                    password = userPassword
                    data = JsonObject(mapOf(
                        "user_type" to JsonPrimitive(role)
                    ))
                }
                saveToken()
                _userState.value = UserState(
                    Loading = false
                )
                _userState.value = UserState(
                    isSignedIn = true
                )
            } catch(e: Exception) {
                _userState.value = UserState(
                    errorMessage = e.message ?: ""
                )
            }
        }
    }

    private fun saveToken() {
        viewModelScope.launch {
            val session = client.auth.currentSessionOrNull()
            val accessToken = session?.accessToken
            sharedPref.saveAccess(accessToken ?: "null")
        }
    }

    private fun getToken(): String? {
        return sharedPref.getAccess()
    }


    fun login(
        userEmail: String,
        userPassword: String,
    ) {
        viewModelScope.launch {
            try {
                _userState.value = UserState(
                    Loading = true
                )
                client.auth.signInWith(Email) {
                    email = userEmail
                    password = userPassword
                }
                saveToken()
                _userState.value = UserState(
                    Loading = false
                )
                _userState.value = UserState(
                    isSignedIn = true
                )
            } catch (e: Exception) {
                _userState.value = UserState(
                    errorMessage = e.message ?: ""
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _userState.value = UserState(
                    Loading = true
                )
                client.auth.signOut()
                sharedPref.clearAll()
                _userState.value = UserState(
                    Loading = false
                )
                _userState.value = UserState(
                    isSignedIn = false
                )
            } catch (e: Exception) {
                _userState.value = UserState(
                    errorMessage = e.message ?: ""
                )            }
        }
    }

}