package com.example.doctorek.ui.screens.preview


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.doctorek.ui.screens.appointementScreens.DoctorDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DoctorDetailScreenPreview() {
    MaterialTheme {
        // Create the mock ViewModel
        val mockViewModel = MockDoctorDetailViewModel()

        // Display the screen with mock data
        DoctorDetailScreen(
            doctorId = "mock_id",
            onBackClick = { /* No-op for preview */ },
            onBookAppointment = { _, _ -> /* No-op for preview */ },
            viewModel = mockViewModel
        )
    }
}