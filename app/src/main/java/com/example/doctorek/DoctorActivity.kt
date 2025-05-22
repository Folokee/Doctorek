package com.example.doctorek

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doctorek.data.repositories.PatientsRepository
import com.example.doctorek.ui.screens.DoctorListScreen
import com.example.doctorek.ui.screens.FavoriteDoctorsScreen
import com.example.doctorek.ui.screens.MainScreen
import com.example.doctorek.ui.screens.ProfileDetailsScreen
import com.example.doctorek.ui.screens.ProfileScreen
import com.example.doctorek.ui.screens.doctorScreens.AppointmentDetailScreen
import com.example.doctorek.ui.screens.doctorScreens.DMainScreen
import com.example.doctorek.ui.screens.doctorScreens.DoctorProfileDetails
import com.example.doctorek.ui.screens.doctorScreens.FullAppointmentsScreen
import com.example.doctorek.ui.screens.doctorScreens.PrescriptionDetailScreen
import com.example.doctorek.ui.screens.doctorScreens.PrescriptionsScreen
import com.example.doctorek.ui.screens.doctorScreens.ThirdStep
import com.example.doctorek.ui.viewmodels.AppointmentsViewModel

class DoctorActivity : ComponentActivity() {
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
            DoctorApp()
        }
    }

    @Composable
    fun DoctorApp() {
        val navController = rememberNavController()
        val viewModel : AppointmentsViewModel = viewModel()
        val appointmentsState = viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = DScreens.Main.route
        ) {
            composable(DScreens.Main.route) {
                DMainScreen(navController)
            }
            composable(
                route = "appointment_details/{appointmentId}",
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                val appointment = appointmentsState.value.appointments.find { it.id == appointmentId }

                appointment?.let {
                    AppointmentDetailScreen(navController = navController, appointment = it)
                } ?: navController.navigateUp()
            }

            composable(
                route = "prescriptions_detail/{prescriptionId}",
                arguments = listOf(navArgument("prescriptionId") { type = NavType.StringType })
            ) { backStackEntry ->
                var prescriptionId : String? = backStackEntry.arguments?.getString("prescriptionId") ?: ""
                if (prescriptionId == "new") prescriptionId = null
                    PrescriptionDetailScreen(prescriptionId, navController)
            }
            composable(DScreens.Appointments.route) {
                FullAppointmentsScreen(navController = navController)
            }
            composable(DScreens.Prescriptions.route) {
                PrescriptionsScreen(navController)
            }
            composable(DScreens.DoctorDetails.route) {
                DoctorProfileDetails(navController)
            }
            composable(DScreens.Availability.route){
                ThirdStep()
            }
        }
    }


}

sealed class DScreens(val route: String) {
    object Main : DScreens("doctor_main")
    object Home : DScreens("doctor_home")
    object Appointments : DScreens("doctor_appointments")
    object Prescriptions : DScreens("doctor_prescriptions")
    object QRCode : DScreens("doctor_qr_code")
    object Profile : DScreens("doctor_profile")
    object DoctorDetails : DScreens("doctor_details")
    object Availability : DScreens("doctor_availability")
}