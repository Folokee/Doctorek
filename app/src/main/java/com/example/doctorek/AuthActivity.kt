package com.example.doctorek

import android.content.Intent
import android.os.Bundle
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

class AuthActivity : ComponentActivity() {
    
    private lateinit var sharedPrefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPrefs = SharedPrefs(applicationContext)
        if (sharedPrefs.getAccess() != null) {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuthApp()
        }
    }

    @Composable
    fun AuthApp() {
        val navController = rememberNavController()
        val sharedPrefs = SharedPrefs(applicationContext)
        val isFirstTime = sharedPrefs.getFirstTime()
        NavHost(
            navController = navController,
            startDestination = if (isFirstTime) AuthScreens.Slideshow.route else AuthScreens.Onboarding.route
        ) {
            composable(AuthScreens.Slideshow.route) { SlideShow(navController) }
            composable(AuthScreens.Signup.route) { SignUpScreen(navController) }
            composable(AuthScreens.Signin.route) { SignInScreen(navController) }
            composable(AuthScreens.Onboarding.route) { Onboarding(navController) }
            composable(AuthScreens.ProfileDetails.route) { ProfileDetailsScreen() }
        }
    }
}

sealed class AuthScreens(val route: String) {
    object Slideshow : Screens("slideshow")
    object Signup : Screens("signup")
    object Signin : Screens("signin")
    object Onboarding : Screens("onboarding")
    object ProfileDetails : Screens("profile_details")
}
