package com.example.doctorek

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.ui.screens.AppointmentsScreen
import com.example.doctorek.ui.screens.FavoriteDoctorsScreen
import com.example.doctorek.ui.screens.HomeScreen
import com.example.doctorek.ui.screens.MainScreen
import com.example.doctorek.ui.screens.Onboarding
import com.example.doctorek.ui.screens.PrescriptionsScreen
import com.example.doctorek.ui.screens.ProfileScreen
import com.example.doctorek.ui.screens.SignInScreen
import com.example.doctorek.ui.screens.SignUpScreen
import com.example.doctorek.ui.screens.SlideShow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false) // Ensure app draws behind system bars

        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightNavigationBars =
            true // For dark icons on light background
        insetsController.isAppearanceLightStatusBars =
            true // For dark status bar icons on white background

        setContent {
            DoctorekApp()
        }
    }

    @Composable
    fun DoctorekApp() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screens.Main.route
        ) {
            composable(Screens.ProfileDetails.route) {
                ProfileScreen(
                    navController = navController,
                )
            }
            composable(Screens.Main.route) {
                MainScreen(navController)
            }
            composable(Screens.FavoriteDoctors.route) {
                FavoriteDoctorsScreen(navController)
            }

        }
    }


}

sealed class Screens(val route: String) {

    object ProfileDetails : Screens("profile")
    object Main : Screens("main")

    object FavoriteDoctors : Screens("favorite_doctors")

    // Nested screens for bottom navigation
    object Home : Screens("home")
    object Appointments : Screens("appointments")
    object Prescriptions : Screens("prescriptions")
    object Profile : Screens("profile")
}

