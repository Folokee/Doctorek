package com.example.doctorek.ui.screens.doctorScreens

import android.app.TimePickerDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.AuthActivity
import com.example.doctorek.DoctorActivity
import com.example.doctorek.MainActivity
import com.example.doctorek.R
import com.example.doctorek.data.models.ContactInfo
import com.example.doctorek.ui.screens.ProfileField
import com.example.doctorek.ui.viewmodels.ProfileViewModel
import kotlin.math.exp

@Composable
fun DoctorProfileDetails(navController: NavController){

    val items = listOf(
        "1st Step", "2nd Step", "3rd Step"
    )

    var selectedItem by remember { mutableStateOf(items[0]) }



    Scaffold { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ){
            when (selectedItem) {
                "1st Step" -> FirstStep(onNext = {
                    selectedItem = items[1]
                })
                "2nd Step" -> SecondStep(onNext = {
                    selectedItem = items[2]
                })
                "3rd Step" -> ThirdStep()
            }
        }

    }

}

@Composable
fun FirstStep(viewModel : ProfileViewModel = viewModel(), onNext: () -> Unit) {
    val context = LocalContext.current
    val uiState by viewModel.profileState.collectAsState()

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onNext()
            viewModel.resetSuccess()
        }
    }

    if (uiState.loading){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.blue),
                modifier = Modifier.size(24.dp)
            )
        }

    } else {
        var fullName by remember { mutableStateOf(uiState.profile.full_name) }
        var address by remember { mutableStateOf(uiState.profile.address) }
        var phone by remember { mutableStateOf(uiState.profile.phone_number) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Help us know more about you")

            Spacer(Modifier.height(30.dp))

            // Form Fields
            ProfileField(
                label = "Full Name",
                placeholder = "Full Name",
                value = fullName ?: "",
                onValueChange = { fullName = it }
            )

            ProfileField(
                label = "Address",
                placeholder = "Address",
                value = address ?: "",
                onValueChange = { address = it }
            )

            ProfileField(
                label = "Phone Number",
                placeholder = "Phone Number",
                value = phone ?: "",
                onValueChange = { phone = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Next Button
            Button(
                onClick = {
                    viewModel.updateProfile(
                        phone_number = phone!!,
                        full_name = fullName!!,
                        address = address!!,
                        avatar_url = ""
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue)
                )
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.blue),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Save",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

}

@Composable
fun SecondStep( onNext: () -> Unit, viewModel: ProfileViewModel = viewModel()){
    var speciality by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("") }
    var hospitalAddress by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var expYears by remember { mutableStateOf("") }

    val uiState by viewModel.profileState.collectAsState()


    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onNext()
            viewModel.resetSuccess()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("We need more information about your profession")

        Spacer(Modifier.height(30.dp))

        ProfileField(
            label = "Speciality",
            placeholder = "Speciality",
            value = speciality,
            onValueChange = { speciality = it }
        )

        Spacer(Modifier.height(8.dp))

        ProfileField(
            label = "Hospital/Cabinet Name",
            placeholder = "Hospital/Cabinet Name",
            value = hospital,
            onValueChange = { hospital = it }
        )
        Spacer(Modifier.height(8.dp))


        ProfileField(
            label = "Hospital/Cabinet Address",
            placeholder = "Hospital/Cabinet Address",
            value = hospitalAddress,
            onValueChange = { hospitalAddress = it }
        )
        Spacer(Modifier.height(8.dp))


        ProfileField(
            label = "Experience Years",
            placeholder = "Experience Years",
            value = expYears,
            onValueChange = { expYears = it }
        )

        Spacer(Modifier.height(8.dp))

        ProfileField(
            label = "Bio",
            placeholder = "Describe yourself",
            value = bio,
            onValueChange = { bio = it }
        )

        Spacer(Modifier.height(8.dp))

        ProfileField(
            label = "Contact Number",
            placeholder = "Contact Number",
            value = number,
            onValueChange = { number = it }
        )
        Spacer(Modifier.height(20.dp))


        // Next Button
        Button(
            onClick = {
                //update doctor profile
                viewModel.createDoctorDetails(
                    specialty = speciality,
                    hospital_name = hospital,
                    hospital_address = hospitalAddress,
                    bio = bio,
                    years_of_experience = expYears.toInt(),
                    contact_information = ContactInfo(
                        phone = number
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.blue)
            )
        ) {
            if (false) {
                CircularProgressIndicator(
                    color = colorResource(R.color.blue),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Next",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ThirdStep() {
    val context = LocalContext.current
    val days = listOf( "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val scrollState = rememberScrollState()

    // State for each day's availability
    val availabilityMap = remember {
        days.associate { day ->
            day to mutableStateOf(DayAvailability(isAvailable = true, startTime = "09:00", endTime = "17:00"))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(
            text = "Availability",
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        days.forEach { day ->
            DayAvailabilityRow(
                day = day,
                availability = availabilityMap[day]!!.value,
                onAvailabilityChange = { newAvailability ->
                    availabilityMap[day]!!.value = newAvailability
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Finish Button
        Button(
            onClick = {
                // Save availability and complete profile setup
                Toast.makeText(context, "Profile setup complete!", Toast.LENGTH_SHORT).show()

                // Navigate to main app flow
                val intent = Intent(context, DoctorActivity::class.java)
                context.startActivity(intent)
                (context as AuthActivity).finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.blue)
            )
        ) {
            Text(
                text = "Finish",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DayAvailabilityRow(
    day: String,
    availability: DayAvailability,
    onAvailabilityChange: (DayAvailability) -> Unit
) {
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = day,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Start Time
                TimePickerField(
                    label = "Start",
                    time = availability.startTime,
                    enabled = availability.isAvailable,
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(12.dp))

                // End Time
                TimePickerField(
                    label = "End",
                    time = availability.endTime,
                    enabled = availability.isAvailable,
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(12.dp))

                // Not Available Checkbox
                Checkbox(
                    checked = !availability.isAvailable,
                    onCheckedChange = { checked ->
                        onAvailabilityChange(availability.copy(isAvailable = !checked))
                    },
                    modifier = Modifier.padding(start = 4.dp)
                )

                Text(
                    text = "Not Available",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }

    // Time picker dialogs (keeping the same functionality)
    if (showStartTimePicker) {
        val currentHour = availability.startTime.split(":")[0].toInt()
        val currentMinute = availability.startTime.split(":")[1].toInt()

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hour, minute ->
                val formattedHour = hour.toString().padStart(2, '0')
                val formattedMinute = minute.toString().padStart(2, '0')
                val newTime = "$formattedHour:$formattedMinute"
                onAvailabilityChange(availability.copy(startTime = newTime))
            },
            currentHour,
            currentMinute,
            true // 24-hour format
        )

        LaunchedEffect(showStartTimePicker) {
            timePickerDialog.show()
            showStartTimePicker = false
        }
    }

    if (showEndTimePicker) {
        val currentHour = availability.endTime.split(":")[0].toInt()
        val currentMinute = availability.endTime.split(":")[1].toInt()

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hour, minute ->
                val formattedHour = hour.toString().padStart(2, '0')
                val formattedMinute = minute.toString().padStart(2, '0')
                val newTime = "$formattedHour:$formattedMinute"
                onAvailabilityChange(availability.copy(endTime = newTime))
            },
            currentHour,
            currentMinute,
            true // 24-hour format
        )

        LaunchedEffect(showEndTimePicker) {
            timePickerDialog.show()
            showEndTimePicker = false
        }
    }
}

@Composable
private fun TimePickerField(
    label: String,
    time: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0EDFF),
                contentColor = colorResource(id = R.color.blue),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.1f),
                disabledContentColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(
                text = time,
                fontSize = 14.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        }
    }
}

// Data class to hold availability information for a day
data class DayAvailability(
    val isAvailable: Boolean,
    val startTime: String,
    val endTime: String
)