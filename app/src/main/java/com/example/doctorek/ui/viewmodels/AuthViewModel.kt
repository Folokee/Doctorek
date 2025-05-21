package com.example.doctorek.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.repositories.AuthRepository

import kotlinx.coroutines.launch

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

                val resp = repository.signUp(userEmail, userPassword, role)
                if (resp.isFailure){
                    _userState.value = UserState(
                        errorMessage = resp.exceptionOrNull()?.message ?: "Signup Failed"
                    )
                    return@launch
                }
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
                val resp = repository.signIn(userEmail, userPassword)
                if (resp.isFailure){
                    _userState.value = UserState(
                        errorMessage = resp.exceptionOrNull()?.message ?: "Signup Failed"
                    )
                    return@launch
                }
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
                val resp = repository.logout()
                if (resp.isFailure){
                    _userState.value = UserState(
                        errorMessage = resp.exceptionOrNull()?.message ?: "Signup Failed"
                    )
                    return@launch
                }
                sharedPref.clearAll()
                _userState.value = UserState(
                    Loading = false,
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