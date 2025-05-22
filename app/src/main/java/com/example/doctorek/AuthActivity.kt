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
        
        // Set window colors and appearance for consistency with MainActivity
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightNavigationBars = true // For dark icons on light background
        insetsController.isAppearanceLightStatusBars = true // For dark status bar icons on white background
        
        WindowCompat.setDecorFitsSystemWindows(window, false) // Ensure app draws behind system bars
        
        setContent {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.White),
                    color = androidx.compose.ui.graphics.Color.White
                ) {
                    AuthApp()
                }
            }
        }

    @Composable
    fun AuthApp() {
        val navController = rememberNavController()
        val sharedPrefs = SharedPrefs(applicationContext)
        val isFirstTime = sharedPrefs.getFirstTime()
        
        NavHost(
            navController = navController,
            startDestination = if (isFirstTime) AuthScreens.Slideshow.route else AuthScreens.Onboarding.route,
            modifier = Modifier.background(androidx.compose.ui.graphics.Color.White)
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
    object Slideshow : AuthScreens("slideshow")
    object Signup : AuthScreens("signup")
    object Signin : AuthScreens("signin")
    object Onboarding : AuthScreens("onboarding")
    object ProfileDetails : AuthScreens("profile_details")
}
