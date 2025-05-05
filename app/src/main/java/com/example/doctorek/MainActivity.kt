package com.example.doctorek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        NavHost(
            navController = navController,
            startDestination = Screens.Slideshow.route
        ){
            composable(Screens.Slideshow.route){ SlideShow(navController) }
        }
    }
}

sealed class Screens(val route : String){
    object Slideshow : Screens("slideshow")
}

