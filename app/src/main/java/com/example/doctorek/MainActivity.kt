package com.example.doctorek

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.ui.screens.AppointmentsScreen
import com.example.doctorek.ui.screens.HomeScreen
import com.example.doctorek.ui.screens.MainScreen
import com.example.doctorek.ui.screens.Onboarding
import com.example.doctorek.ui.screens.PrescriptionsScreen
import com.example.doctorek.ui.screens.ProfileDetailsScreen
import com.example.doctorek.ui.screens.SignInScreen
import com.example.doctorek.ui.screens.SignUpScreen
import com.example.doctorek.ui.screens.SlideShow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false) // Ensure app draws behind system bars

        // Set system navigation bar to transparent and icons to dark
        window.navigationBarColor = Color.TRANSPARENT
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightNavigationBars = true // For dark icons on light background

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
            composable(Screens.Signin.route) { 
                SignInScreen(
                    navController = navController,
                    onSignInSuccess = { navController.navigate(Screens.Main.route) }
                ) 
            }
            composable(Screens.Onboarding.route) { Onboarding(navController) }
            composable(Screens.ProfileDetails.route) { 
                ProfileDetailsScreen(
                    navController = navController,

                ) 
            }
            composable(Screens.Main.route) { 
                MainScreen(navController) 
            }
        }
    }
}

sealed class Screens(val route : String){
    object Slideshow : Screens("slideshow")
    object Signup : Screens("signup")
    object Signin : Screens("signin")
    object Onboarding : Screens("onboarding")
    object ProfileDetails : Screens("profile_details")
    object Main : Screens("main")
    
    // Nested screens for bottom navigation
    object Home : Screens("home")
    object Appointments : Screens("appointments")
    object Prescriptions : Screens("prescriptions")
    object Profile : Screens("profile")
}

