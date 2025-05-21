package com.example.doctorek.ui.screens.appointementScreens
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doctorek.R
import com.example.doctorek.data.models.AppointmentSlot
import com.example.doctorek.data.models.TimePeriod
import com.example.doctorek.data.repositories.AppointmentRepository
import com.example.doctorek.ui.viewmodels.BookAppointmentViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    doctorId: String,
    selectedDate: LocalDate,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    viewModel: BookAppointmentViewModel = viewModel(
        factory = BookAppointmentViewModel.Factory(
            doctorId = doctorId,
            selectedDate = selectedDate,
            repository = AppointmentRepository()
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Book Appointment",
                        fontFamily = sourceSansPro,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.error != null) {
                ErrorMessage(
                    message = state.error!!,
                    onRetry = { viewModel.loadAvailableTimeSlots() },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Display selected date
                    Text(
                        text = state.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy", Locale.ENGLISH)),
                        fontFamily = sourceSansPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Time period toggles (Morning/Evening)
                    TimePeriodSelector(
                        selectedPeriod = state.selectedTimePeriod,
                        onPeriodSelected = { viewModel.selectTimePeriod(it) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Choose hour text
                    Text(
                        text = "Choose the Hour",
                        fontFamily = sourceSansPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time slots grid
                    val currentSection = state.availableTimeSections.find {
                        it.period == state.selectedTimePeriod
                    }
                    if (currentSection != null) {
                        TimeSlotGrid(
                            slots = currentSection.slots,
                            selectedTime = state.selectedTime,
                            onTimeSelected = { viewModel.selectTime(it) }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Next button
                    Button(
                        onClick = {
                            // In a real app, you'd use the patientId from user session
                            viewModel.submitAppointment(
                                patientId = "current_user_id",
                                onSuccess = onNextClick,
                                onError = { /* Show error message */ }
                            )
                        },
                        enabled = state.selectedTime != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3E7BFA),
                            disabledContainerColor = Color(0xFF3E7BFA).copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "Next",
                            fontFamily = sourceSansPro,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun TimePeriodSelector(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TimePeriodButton(
            title = "Morning",
            isSelected = selectedPeriod == TimePeriod.MORNING,
            onClick = { onPeriodSelected(TimePeriod.MORNING) },
            modifier = Modifier.weight(1f),
            imageResId= R.drawable.sunny
        )

        TimePeriodButton(
            title = "Evening",
            isSelected = selectedPeriod == TimePeriod.EVENING,
            onClick = { onPeriodSelected(TimePeriod.EVENING) },
            modifier = Modifier.weight(1f),
            imageResId = R.drawable.night
        )
    }
}

@Composable
fun TimePeriodButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes imageResId: Int
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF3E7BFA) else Color(0xFFD1D5DB),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) Color(0xFF3E7BFA).copy(alpha = 0.1f) else Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(
                    if (isSelected) Color(0xFF3E7BFA) else Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                fontFamily = sourceSansPro,
                color = if (isSelected) Color(0xFF3E7BFA) else Color.Gray,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotGrid(
    slots: List<AppointmentSlot>,
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Create rows with 3 time slots each
        val rows = slots.chunked(3)
        rows.forEach { rowSlots ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowSlots.forEach { slot ->
                    TimeSlotButton(
                        time = slot.time,
                        isAvailable = slot.isAvailable,
                        isSelected = selectedTime == slot.time,
                        onClick = { if (slot.isAvailable) onTimeSelected(slot.time) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Add empty slots if needed to maintain 3 slots per row
                repeat(3 - rowSlots.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSlotButton(
    time: LocalTime,
    isAvailable: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("HH.mm")
    val amPmFormatter = DateTimeFormatter.ofPattern("HH.mm a")

    val backgroundColor = when {
        isSelected -> Color(0xFF3E7BFA)
        !isAvailable -> Color(0xFFF3F4F6)
        else -> Color.White
    }

    val textColor = when {
        isSelected -> Color.White
        !isAvailable -> Color(0xFFBDBDBD)
        else -> Color(0xFF2972FE)
    }

    val borderColor = when {
        isSelected -> Color(0xFF3E7BFA)
        !isAvailable -> Color(0xFFE5E7EB)
        else -> Color(0xFF2972FE)
    }


    Box(
        modifier = modifier
            .height(48.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable(enabled = isAvailable, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            // Display time in format like "09.00 AM"
            text = time.format(DateTimeFormatter.ofPattern("HH.mm")) + " " +
                    if (time.hour < 12) "AM" else "PM",
            fontFamily = sourceSansPro,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun BookAppointmentScreenPreview() {
    val mockViewModel = BookAppointmentViewModel(
        doctorId = "doctor_123",
        selectedDate = LocalDate.now(),
        repository = AppointmentRepository()
    )


        BookAppointmentScreen(
            doctorId = "doctor_123",
            selectedDate = LocalDate.now(),
            onBackClick = {},
            onNextClick = {},
            viewModel = mockViewModel
        )
}