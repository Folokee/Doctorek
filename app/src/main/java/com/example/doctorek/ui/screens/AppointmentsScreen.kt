package com.example.doctorek.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctorek.ui.components.DoctorekAppBar

@Composable
fun AppointmentsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            DoctorekAppBar(title = "My Appointments")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Appointments Coming Soon",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}
