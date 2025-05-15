package com.example.doctorek
import android.os.Build
import androidx.navigation.navArgument
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.repositories.AppointmentRepository
import com.example.doctorek.ui.screens.Onboarding
import com.example.doctorek.ui.screens.ProfileDetailsScreen
import com.example.doctorek.ui.screens.SignInScreen
import com.example.doctorek.ui.screens.SignUpScreen
import com.example.doctorek.ui.screens.SlideShow
import com.example.doctorek.ui.screens.appointementScreens.BookAppointmentScreen
import com.example.doctorek.ui.screens.appointementScreens.BookAppointmentScreenPreview
import com.example.doctorek.ui.screens.appointementScreens.DoctorDetailScreen
import com.example.doctorek.ui.screens.appointementScreens.PatientDetailsScreen
import com.example.doctorek.ui.screens.preview.DoctorDetailScreenPreview
import com.example.doctorek.ui.viewmodels.BookAppointmentViewModel
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorekApp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun DoctorekApp() {
        val navController = rememberNavController()
        val sharedPrefs = SharedPrefs(applicationContext)
        val isFirstTime = sharedPrefs.getFirstTime()
        NavHost(
            navController = navController,
            startDestination = if(isFirstTime) Screens.Slideshow.route else Screens.DoctorDetails
        ){
            composable(Screens.Slideshow.route){ SlideShow(navController) }
            composable(Screens.Signup.route){ SignUpScreen(navController) }
            composable(Screens.Signin.route) { SignInScreen(navController) }
            composable(Screens.Onboarding.route) { Onboarding(navController) }
            composable(Screens.ProfileDetails.route) { ProfileDetailsScreen() }

            composable(
                route = "doctorDetail/{doctorId}",
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
               /*DoctorDetailScreen(
                    doctorId = doctorId,
                    onBackClick = { navController.popBackStack() },
                    onBookAppointment = { id, date ->
                        navController.navigate(Screens.BookAppointment(id, date).route)
                    }
                )*/
                DoctorDetailScreenPreview()
            }

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
                        repository = AppointmentRepository()
                    )
                )

                val showPatientDetails = remember { mutableStateOf(false) }

                if (showPatientDetails.value) {
                    PatientDetailsScreen(
                        onBackClick = { showPatientDetails.value = false },
                        onNextClick = {
                            navController.navigate(Screens.AppointmentConfirmation(doctorId).route)
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

sealed class Screens(val route : String){
    object Slideshow : Screens("slideshow")
    object Signup : Screens("signup")
    object Signin : Screens("signin")
    object Onboarding : Screens("onboarding")
    object ProfileDetails : Screens("profile_details")
    object DoctorDetails : Screens("doctor_details")

    // ADDING MY ROUTE HERE lahcen:
    data class DoctorDetail(val doctorId: String) : Screens("doctorDetail/$doctorId")
    data class BookAppointment(val doctorId: String, val date: String) : Screens("bookAppointment/$doctorId/$date")
    data class AppointmentConfirmation(val doctorId: String) : Screens("appointmentConfirmation/$doctorId")

}

