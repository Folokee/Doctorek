package com.example.doctorek.ui.screens.appointementScreens
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doctorek.ui.components.AppointmentFailureModal
import com.example.doctorek.ui.components.AppointmentSuccessModal
import com.example.doctorek.ui.viewmodels.BookAppointmentViewModel

import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    viewModel: BookAppointmentViewModel,
    doctorName: String = ""
) {
    val patientDetails by viewModel.patientDetails.collectAsStateWithLifecycle()
    val appointmentStatus by viewModel.appointmentStatus.collectAsStateWithLifecycle()
    
    // Dialog states
    when (appointmentStatus) {
        is BookAppointmentViewModel.AppointmentStatus.Success -> {
            AppointmentSuccessModal(
                doctorName = doctorName,
                onDismiss = {
                    viewModel.resetAppointmentStatus()
                },
                onBackToHome = {
                    viewModel.resetAppointmentStatus()
                    onNextClick() // This will trigger navigation to Main screen
                }
            )
        }
        is BookAppointmentViewModel.AppointmentStatus.Failure -> {
            AppointmentFailureModal(
                doctorName = doctorName,
                onDismiss = {
                    viewModel.resetAppointmentStatus()
                },
                onTryAgain = {
                    viewModel.resetAppointmentStatus()
                    onBackClick() // Go back to fix issues
                }
            )
        }
        else -> {
            // No dialog to show
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Appointment Details",
                        fontFamily = sourceSansPro,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2972FE)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Problem Description
            FormLabel(text = "Reason for Visit")

            OutlinedTextField(
                value = patientDetails.problem,
                onValueChange = { viewModel.updateProblem(it) },
                placeholder = { Text("Describe your symptoms or reason for visit") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF3E7BFA),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Notes
            FormLabel(text = "Additional Notes")

            OutlinedTextField(
                value = patientDetails.additionalNotes ?: "",
                onValueChange = { viewModel.updateAdditionalNotes(it) },
                placeholder = { Text("Add any additional information for the doctor") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF3E7BFA),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // Book Appointment Button
            Button(
                onClick = {
                    val userId = "current_user_id" // In a real app, get from SharedPrefs
                    viewModel.submitAppointment(
                        patientId = userId,
                        onSuccess = { /* Success will show modal */ },
                        onError = { /* Handle error */ }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E7BFA)
                )
            ) {
                Text(
                    text = "Book Appointment",
                    fontFamily = sourceSansPro,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontFamily = sourceSansPro,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = Color.Black,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
