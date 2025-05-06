package com.example.doctorek

import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.ui.screens.Onboarding
import com.example.doctorek.ui.screens.ProfileDetailsScreen
import com.example.doctorek.ui.screens.SignInScreen
import com.example.doctorek.ui.screens.SignUpScreen
import com.example.doctorek.ui.screens.SlideShow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorekApp()
        }
    }

    @Composable
    fun DoctorekApp() {
        val navController = rememberNavController()
        val sharedPrefs = SharedPrefs(applicationContext)
        val isFirstTime = sharedPrefs.getFirstTime()
        NavHost(
            navController = navController,
            startDestination = if(isFirstTime) Screens.Slideshow.route else Screens.Onboarding.route
        ){
            composable(Screens.Slideshow.route){ SlideShow(navController) }
            composable(Screens.Signup.route){ SignUpScreen(navController) }
            composable(Screens.Signin.route) { SignInScreen(navController) }
            composable(Screens.Onboarding.route) { Onboarding(navController) }
            composable(Screens.ProfileDetails.route) { ProfileDetailsScreen() }
        }
    }
}

sealed class Screens(val route : String){
    object Slideshow : Screens("slideshow")
    object Signup : Screens("signup")
    object Signin : Screens("signin")
    object Onboarding : Screens("onboarding")
    object ProfileDetails : Screens("profile_details")
}

