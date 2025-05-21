package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.auth.SupabaseClient.client

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

                if (role == "patient") {
                    client.gotrue.signUpWith(Email){
                        email = userEmail
                        password = userPassword
                    }
                } else {
                    client.gotrue.signUpWith(Email) {
                        email = userEmail
                        password = userPassword
                        data = JsonObject(
                            mapOf(
                                "user_type" to JsonPrimitive(role)
                            )
                        )
                    }
                }
                saveToken()
                sharedPref.save("user_email", userEmail)
                saveId()
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
            val session = client.gotrue.currentSessionOrNull()
            val accessToken = session?.accessToken
            sharedPref.saveAccess(accessToken ?: "null")
        }
    }

    private fun saveId(){
        viewModelScope.launch {
            val session = client.gotrue.currentSessionOrNull()
            val userId = session?.user?.id
            sharedPref.save("user_id", userId ?: "null")
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
                client.gotrue.loginWith(Email) {
                    email = userEmail
                    password = userPassword
                }
                saveToken()
                sharedPref.save("user_email", userEmail)
                saveId()
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
                client.gotrue.logout()
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