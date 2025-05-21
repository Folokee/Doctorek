package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.auth.SupabaseClient.client
import com.example.doctorek.data.repositories.AuthRepository

import kotlinx.coroutines.launch


import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.ktor.utils.io.concurrent.shared
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
    private val repository = AuthRepository(application.applicationContext)

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

                repository.signUp(userEmail, userPassword, role)
                _userState.value = UserState(
                    Loading = false
                )
                _userState.value = UserState(
                    isSignedIn = true
                )
            } catch(e: Exception) {
                _userState.value = UserState(
                    errorMessage = e.message ?: "Signup Failed"
                )
            }
        }
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
                repository.signIn(userEmail, userPassword)
                _userState.value = UserState(
                    Loading = false
                )
                _userState.value = UserState(
                    isSignedIn = true
                )
            } catch (e: Exception) {
                _userState.value = UserState(
                    errorMessage = e.message ?: "Login Failed"
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
                repository.logout()
                sharedPref.clearAll()
                _userState.value = UserState(
                    Loading = false
                )
                _userState.value = UserState(
                    isSignedIn = false
                )
            } catch (e: Exception) {
                _userState.value = UserState(
                    errorMessage = e.message ?: "Logout Error"
                )
            }
        }
    }

}