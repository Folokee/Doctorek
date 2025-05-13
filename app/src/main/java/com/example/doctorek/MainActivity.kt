package com.example.doctorek

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.doctorapp.ui.screens.PatientDetailsScreenPreview
import com.example.tdm_project.navigation.AppNavigation
import com.example.tdm_project.ui.screens.BookAppointmentScreenPreview
import com.example.tdm_project.ui.theme.TDM_PROJECTTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientDetailsScreenPreview()
        }
    }
}