package com.example.doctorek

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorek.data.auth.SharedPrefs
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.doctorek.data.repositories.AppointmentRepository
import com.example.doctorek.ui.screens.DoctorListScreen
import com.example.doctorek.ui.screens.FavoriteDoctorsScreen
import com.example.doctorek.ui.screens.MainScreen
import com.example.doctorek.ui.screens.ProfileScreen
import com.example.doctorek.ui.screens.appointementScreens.BookAppointmentScreen
import com.example.doctorek.ui.screens.appointementScreens.DoctorDetailScreen
import com.example.doctorek.ui.screens.appointementScreens.PatientDetailsScreen
import com.example.doctorek.ui.viewmodels.BookAppointmentViewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    lateinit var sharedPrefs: SharedPrefs

    @RequiresApi(Build.VERSION_CODES.O)
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

        sharedPrefs = SharedPrefs(applicationContext)

        setContent {
            DoctorekApp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            composable(
                route = Screens.DoctorList.route + "?category={category}",
                arguments = listOf(navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                DoctorListScreen(
                    navController = navController,
                    initialCategoryFilter = backStackEntry.arguments?.getString("category")
                )
            }

            // New screens for doctor appointment flow

            // Doctor Details Screen
            composable(
                route = "doctorDetail/{doctorId}",
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                DoctorDetailScreen(
                    doctorId = doctorId,
                    onBackClick = { navController.popBackStack() },
                    onBookAppointment = { id, date ->
                        navController.navigate("bookAppointment/$id/$date")
                    }
                )
            }
            // Book Appointment Screen
            composable(
                route = "bookAppointment/{doctorId}/{date}",
                arguments = listOf(
                    navArgument("doctorId") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                val dateStr = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
                val selectedDate = try {
                    LocalDate.parse(dateStr)
                } catch (e: Exception) {
                    LocalDate.now()
                }

                val viewModel = viewModel<BookAppointmentViewModel>(
                    factory = BookAppointmentViewModel.Factory(
                        doctorId = doctorId,
                        selectedDate = selectedDate,
                        repository = AppointmentRepository(
                            context = applicationContext
                        )
                    )
                )

                val showPatientDetails = remember { mutableStateOf(false) }

                if (showPatientDetails.value) {
                    PatientDetailsScreen(
                        onBackClick = { showPatientDetails.value = false },
                        onNextClick = {
                            navController.navigate(Screens.Main.route) {
                                popUpTo(Screens.Main.route) { inclusive = true }
                            }
                        },
                        viewModel = viewModel
                    )
                } else {
                    BookAppointmentScreen(
                        doctorId = doctorId,
                        selectedDate = selectedDate,
                        onBackClick = { navController.popBackStack() },
                        onNextClick = { showPatientDetails.value = true },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

sealed class Screens(val route: String) {

    object ProfileDetails : Screens("profile")
    object Main : Screens("main")

    object FavoriteDoctors : Screens("favorite_doctors")
    object DoctorList : Screens("doctors_list") // New screen route

    // Nested screens for bottom navigation
    object Home : Screens("home")
    object Appointments : Screens("appointments")
    object Prescriptions : Screens("prescriptions")
    object Profile : Screens("profile")

    // Screen routes with parameters
    data class DoctorDetail(val doctorId: String) : Screens("doctorDetail/$doctorId")
    data class BookAppointment(val doctorId: String, val date: String) : Screens("bookAppointment/$doctorId/$date")
    data class AppointmentConfirmation(val doctorId: String) : Screens("appointmentConfirmation/$doctorId")
}