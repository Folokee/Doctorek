package com.example.doctorek.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorek.data.repositories.AppointmentRepository
import com.example.doctorek.ui.screens.appointementScreens.BookAppointmentScreen
import com.example.doctorek.ui.screens.appointementScreens.PatientDetailsScreen
import com.example.doctorek.ui.viewmodels.BookAppointmentViewModel
import java.time.LocalDate

// Routes for the appointment flow
sealed class AppointmentScreens(val route: String) {
    object BookAppointment : AppointmentScreens("book_appointment")
    object PatientDetails : AppointmentScreens("patient_details")
    object Confirmation : AppointmentScreens("confirmation")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentFlowCoordinator(
    doctorId: String,
    selectedDate: LocalDate,
    navController: NavHostController = rememberNavController(),
    onCompleted: () -> Unit,
    onCancel: () -> Unit
) {
    // Create a shared view model for both screens
    val viewModel: BookAppointmentViewModel = viewModel(
        factory = BookAppointmentViewModel.Factory.create(
            doctorId = doctorId,
            selectedDate = selectedDate,
            context = androidx.compose.ui.platform.LocalContext.current
        )
    )

    NavHost(
        navController = navController,
        startDestination = AppointmentScreens.BookAppointment.route
    ) {
        composable(AppointmentScreens.BookAppointment.route) {
            BookAppointmentScreen(
                doctorId = doctorId,
                selectedDate = selectedDate,
                onBackClick = { onCancel() },
                onNextClick = {
                    navController.navigate(AppointmentScreens.PatientDetails.route)
                },
                viewModel = viewModel
            )
        }

        composable(AppointmentScreens.PatientDetails.route) {
            PatientDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { onCompleted() },
                viewModel = viewModel
            )
        }
    }
}
