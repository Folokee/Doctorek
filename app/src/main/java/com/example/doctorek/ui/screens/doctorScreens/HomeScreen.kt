package com.example.doctorek.ui.screens.doctorScreens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.DScreens
import com.example.doctorek.R
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.ui.components.DoctorekAppBar
import com.example.doctorek.ui.viewmodels.AppointmentsViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(navController: NavController, viewModel: AppointmentsViewModel = viewModel()) {
    val state = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sharedPrefs = SharedPrefs(context)
    val m_name = sharedPrefs.getName()


    Scaffold(
        topBar = { DoctorekAppBar(m_name ?:"Dr. Smith") },
        containerColor = colorResource(R.color.white)
    ) {innerPadding ->
        if (state.value.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val appointments = state.value.appointments
            Log.d("Appointments", "Appointments: $appointments")

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Statistics Section
                item {
                    StatisticsSection(
                        appointmentsToday = appointments.size,
                        prescriptionsCreated = 8,
                        completedAppointments = appointments.count { it.state == AppointmentState.DONE }
                    )
                }

                // Appointments Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Appointments",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.nav_bar_active_item)
                        )

                        TextButton(
                            onClick = {
                                navController.navigate(DScreens.Appointments.route)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colorResource(id = R.color.nav_bar_active_item)
                            )
                        ) {
                            Text("View All")
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "View All"
                            )
                        }
                    }
                }

                // Today's Appointments
                if (appointments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No appointments for today",
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    items(appointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onClick = {
                                // Navigate to appointment details screen
                                navController.navigate("appointment_details/${appointment.id}")
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsSection(appointmentsToday: Int, prescriptionsCreated: Int, completedAppointments: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(colorResource(R.color.white)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                icon = Icons.Default.DateRange,
                title = "Today's Appointments",
                value = appointmentsToday.toString(),
                modifier = Modifier.weight(1f),
                backgroundColor = colorResource(id = R.color.light_blue).copy(alpha = 0.2f),
                iconTint = colorResource(id = R.color.nav_bar_active_item)
            )

            StatCard(
                icon = Icons.Default.ListAlt,
                title = "Prescriptions",
                value = prescriptionsCreated.toString(),
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xFFE6F7FF),
                iconTint = Color(0xFF0288D1)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                icon = Icons.Default.Check,
                title = "Completed",
                value = completedAppointments.toString(),
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xFFE0F2F1),
                iconTint = Color(0xFF00897B)
            )

            StatCard(
                icon = Icons.Default.Schedule,
                title = "Avg. Wait Time",
                value = "14 min",
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xFFFFF8E1),
                iconTint = Color(0xFFFFB300)
            )
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint
            )

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.nav_bar_active_item)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onClick: (Appointment) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(appointment) },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Patient Avatar
            Box(
                modifier = Modifier
                    .height(74.dp)
                    .width(60.dp)
                    .clip(RectangleShape)
                    .background(colorResource(id = R.color.light_blue).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // Replace with actual avatar when available
                Text(
                    text = appointment.patientName.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(id = R.color.nav_bar_active_item),
                    fontWeight = FontWeight.Bold
                )
            }

            // Patient info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = appointment.patientName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.black)
                )

                Text(
                    text = "Today ${appointment.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                            appointment.endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Appointment status icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .padding(end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = appointment.state.icon,
                    contentDescription = appointment.state.name,
                    tint = appointment.state.iconColor
                )
            }
        }
    }
}

// Data classes and enums
enum class AppointmentState(
    val icon: ImageVector,
    val iconColor: Color,
    val value : String
) {
    SCHEDULED(
        Icons.Default.Schedule,
        Color(0xFF2196F3),
        "scheduled"
    ),
    DONE(
        Icons.Default.Check,
        Color(0xFF4CAF50),
        "completed"
    ),
    CANCELLED(
        Icons.Default.Close,
        Color(0xFFF44336),
        "cancelled"
    ),
    MISSED(
        Icons.Default.Warning,
        Color(0xFFFF9800),
        "no_show"
    ),
    CONFIRMED(
        Icons.Default.Check,
        Color(0xFF4CAF50),
        "confirmed"
    )
}

data class Appointment(
    val id: String,
    val patientName: String,
    val patientAvatar: String? = null, // URL or resource ID for avatar
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val state: AppointmentState,
    val patientNote : String = "",
)

