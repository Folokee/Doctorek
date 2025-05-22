package com.example.doctorek

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.repositories.Role
import com.example.doctorek.ui.screens.Onboarding
import com.example.doctorek.ui.screens.ProfileDetailsScreen
import com.example.doctorek.ui.screens.SignInScreen
import com.example.doctorek.ui.screens.SignUpScreen
import com.example.doctorek.ui.screens.SlideShow
import com.example.doctorek.ui.screens.doctorScreens.DoctorProfileDetails

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPrefs = SharedPrefs(applicationContext)
        if (sharedPrefs.getName() != null) {
            if (sharedPrefs.getAccess() != null && sharedPrefs.getType() != null && sharedPrefs.getType()
                    ?.compareTo(
                        Role.Patient.role
                    ) == 0
            ) {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                finish()
            }

            if (sharedPrefs.getAccess() != null && sharedPrefs.getType() != null && sharedPrefs.getType()
                    ?.compareTo(Role.Doctor.role) == 0
            ) {
                val intent = Intent(this, DoctorActivity::class.java)
                this.startActivity(intent)
                finish()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Set window colors and appearance for consistency with MainActivity
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightNavigationBars = true // For dark icons on light background
        insetsController.isAppearanceLightStatusBars = true // For dark status bar icons on white background
        
        WindowCompat.setDecorFitsSystemWindows(window, false) // Ensure app draws behind system bars
        
        setContent {
            AuthApp()
        }
    }

    @Composable
    fun AuthApp() {
        val navController = rememberNavController()
        val sharedPrefs = SharedPrefs(applicationContext)
        val isFirstTime = sharedPrefs.getFirstTime()
        val completedSetup = sharedPrefs.getName() != null
        val isDoctor = sharedPrefs.getType() != null && sharedPrefs.getType()?.compareTo(Role.Doctor.role) == 0
        val isPatient = sharedPrefs.getType() != null && sharedPrefs.getType()?.compareTo(Role.Patient.role) == 0
        var start = ""
        if (isFirstTime){
            start = AuthScreens.Slideshow.route
        } else if (!completedSetup && isPatient){
            start = AuthScreens.ProfileDetails.route
        } else if (!completedSetup && isDoctor){
            start = AuthScreens.DoctorDetails.route
        } else {
            start = AuthScreens.Onboarding.route
        }
        NavHost(
            navController = navController,
            startDestination = start
        ){
            composable(AuthScreens.Slideshow.route){ SlideShow(navController) }
            composable(AuthScreens.Signup.route){ SignUpScreen(navController) }
            composable(AuthScreens.Signin.route) { SignInScreen(navController) }
            composable(AuthScreens.Onboarding.route) { Onboarding(navController) }
            composable(AuthScreens.ProfileDetails.route) { ProfileDetailsScreen() }
            composable(AuthScreens.DoctorDetails.route) {
                DoctorProfileDetails(navController)
            }
        }
    }


sealed class AuthScreens(val route : String){
    object Slideshow : Screens("slideshow")
    object Signup : Screens("signup")
    object Signin : Screens("signin")
    object Onboarding : Screens("onboarding")
    object ProfileDetails : Screens("profile_details")
    object DoctorDetails : DScreens("doctor_details")
}
