package com.example.tdm_project.navigation
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.doctorapp.ui.screens.PatientDetailsScreen
import com.example.tdm_project.repositories.AppointmentRepository
import com.example.tdm_project.ui.screens.BookAppointmentScreen
import com.example.tdm_project.ui.screens.DoctorDetailScreen
import com.example.tdm_project.ui.screens.DoctorListScreen
import com.example.tdm_project.viewModels.BookAppointmentViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "doctorList"
    ) {
        composable("doctorList") {
            // For now, we'll create a placeholder screen
            // Later you can replace this with your actual doctor list screen
            DoctorListScreen(
                onDoctorClick = { doctorId ->
                    navController.navigate("doctorDetail/$doctorId")
                }
            )
        }

        composable(
            route = "doctorDetail/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") as String
            val onBookAppointment: (String, String) -> Unit = { doctorId, dateStr ->
                navController.navigate("bookAppointment/$doctorId/$dateStr")
            }
            DoctorDetailScreen(
                onBackClick = { navController.popBackStack() },
                onBookAppointment = onBookAppointment,

                doctorId = doctorId
            )
        }


        // Add Book Appointment route
        composable(
            route = "bookAppointment/{doctorId}/{date}",
            arguments = listOf(
                navArgument("doctorId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            val dateStr = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()

            // Parse the date string to LocalDate
            val selectedDate = try {
                LocalDate.parse(dateStr)
            } catch (e: Exception) {
                LocalDate.now() // Fallback to today if parsing fails
            }


            val viewModel = viewModel<BookAppointmentViewModel>(
                factory = BookAppointmentViewModel.Factory(
                    doctorId = doctorId,
                    selectedDate = selectedDate,
                    repository = AppointmentRepository()
                )
            )

            // This boolean controls which screen to show
            val showPatientDetails = remember { mutableStateOf(false) }

            if (showPatientDetails.value) {
                PatientDetailsScreen(
                    onBackClick = { showPatientDetails.value = false },
                    onNextClick = {
                        navController.navigate("appointmentConfirmation/$doctorId")
                    },
                    viewModel = viewModel
                )
            } else {
                BookAppointmentScreen(  doctorId = doctorId,
                    selectedDate = selectedDate,
                    onBackClick = { navController.popBackStack() },
                    onNextClick = { showPatientDetails.value = true },
                    viewModel = viewModel
                )
            }
        }

    }
}