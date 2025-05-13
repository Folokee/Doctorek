// file: app/src/main/java/com/example/doctorapp/ui/screens/PatientDetailsScreen.kt
package com.example.doctorapp.ui.screens
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
import com.example.tdm_project.models.AgeRanges
import com.example.tdm_project.models.GenderOptions
import com.example.tdm_project.repositories.AppointmentRepository
import com.example.tdm_project.ui.components.AppointmentFailureModal
import com.example.tdm_project.ui.components.AppointmentSuccessModal
import com.example.tdm_project.ui.screens.sourceSansPro
import com.example.tdm_project.ui.theme.TDM_PROJECTTheme
import com.example.tdm_project.viewModels.BookAppointmentViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    viewModel: BookAppointmentViewModel,
    doctorName:String="Lahcen BENCHAREF"
) {
    val patientDetails by viewModel.patientDetails.collectAsStateWithLifecycle()
    val selectedAgeRangeIndex by viewModel.selectedAgeRangeIndex.collectAsStateWithLifecycle()
    val appointmentStatus by viewModel.appointmentStatus.collectAsStateWithLifecycle()
    when (appointmentStatus) {
        is BookAppointmentViewModel.AppointmentStatus.Success -> {
            // Using your existing AppointmentSuccessModal with proper callbacks
            AppointmentSuccessModal(
                doctorName = doctorName,
                onDismiss = {
                    // Reset the state in ViewModel when dismissing
                    viewModel.resetAppointmentStatus()
                },
                onBackToHome = {
                    // Reset the state and navigate back to home
                    viewModel.resetAppointmentStatus()
                    onBackClick()
                }
            )
        }
        is BookAppointmentViewModel.AppointmentStatus.Failure -> {
            // Using your ErrorModal component with proper callbacks
            AppointmentFailureModal(
                doctorName = doctorName,
                onDismiss = {
                    // Reset the state in ViewModel when dismissing
                    viewModel.resetAppointmentStatus()
                },
                onTryAgain = {
                    // Reset the state and potentially retry the submission
                    viewModel.resetAppointmentStatus()
                    // Optionally retry submission
                    // viewModel.submitAppointment("current_user")
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
                        text = "Patient Details",
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
                            tint = Color(0xFF2972FE) // custom arrow color
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
            // Full Name Field
            FormLabel(text = "Full Name")

            OutlinedTextField(
                value = patientDetails.fullName,
                onValueChange = { viewModel.updateFullName(it) },
                placeholder = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF3E7BFA),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Age Range Selection
            FormLabel(text = "Select your age Range")

            AgeRangeSelector(
                selectedIndex = selectedAgeRangeIndex,
                onAgeRangeSelected = { viewModel.selectAgeRange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Field
            FormLabel(text = "Phone Number")

            OutlinedTextField(
                value = patientDetails.phoneNumber,
                onValueChange = { viewModel.updatePhoneNumber(it) },
                placeholder = { Text("Email") }, // This should be "Phone" but matching the image
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF3E7BFA),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Selection
            FormLabel(text = "Gender")

            GenderDropdown(
                selectedGender = patientDetails.gender,
                onGenderSelected = { viewModel.updateGender(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Problem Description
            FormLabel(text = "Write Your Problem")

            OutlinedTextField(
                value = patientDetails.problem,
                onValueChange = { viewModel.updateProblem(it) },
                placeholder = { Text("Tell Doctor About Your Problem") },
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

            Spacer(modifier = Modifier.weight(1f))

            // Next Button
            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E7BFA)
                )
            ) {
                Text(
                    text = "Next",
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

@Composable
fun AgeRangeSelector(
    selectedIndex: Int?,
    onAgeRangeSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AgeRanges.ranges.forEachIndexed { index, range ->
            val isSelected = selectedIndex == index
            AgeRangeButton(
                range = range,
                isSelected = isSelected,
                onClick = { onAgeRangeSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AgeRangeButton(
    range: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF2972FE) else Color(0xFFD1D5DB),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = range,
            fontFamily = sourceSansPro,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color(0xFF3E7BFA) else Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Email") }, // Matching the image, though odd placeholder
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Gender"
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3E7BFA),
                unfocusedBorderColor = Color(0xFFE5E7EB)
            ),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            GenderOptions.options.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Preview function for testing
@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PatientDetailsScreenPreview() {
    // Create a mock ViewModel for preview
    val mockViewModel = BookAppointmentViewModel(doctorId = "1", selectedDate = LocalDate.now(), repository = AppointmentRepository())

    TDM_PROJECTTheme {
       AppointmentFailureModal(doctorName = "lahcen BENCHAREF", onDismiss = {}, onTryAgain = {})
    }
}

