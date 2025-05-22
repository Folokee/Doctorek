package com.example.doctorek.ui.screens.doctorScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.ui.viewmodels.AppointmentsViewModel
import java.time.format.DateTimeFormatter

data class Status(
    val name : String,
    val value : String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    navController: NavController? = null,
    appointment: Appointment,
    viewModel : AppointmentsViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val primaryColor = colorResource(id = R.color.nav_bar_active_item)
    val statusOptions = listOf(
        Status("Confirmed", "confirmed"),
        Status("Scheduled", "scheduled"),
        Status("Cancelled", "cancelled"),
        Status("Completed", "completed"),
        Status("No Show", "no_show")
    )
    var selectedStatus by remember { mutableStateOf(appointment.state.value) }
    var expanded by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = primaryColor
                )
            )
        },
        containerColor = colorResource(R.color.white)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient info header
            PatientInfoHeader(appointment = appointment)

            // Status Dropdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colorResource(R.color.gray))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Appointment Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(id = R.color.black)
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedStatus,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    Modifier.clickable { expanded = true }
                                )
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.name) },
                                    onClick = {
                                        selectedStatus = status.value
                                        expanded = false
                                        viewModel.updateStatus(appointment.id, status.value)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Patient note
            PatientNoteCard(appointment = appointment)
        }
    }
}

@Composable
fun PatientInfoHeader(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colorResource(R.color.gray))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.light_blue).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appointment.patientName.first().toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorResource(id = R.color.nav_bar_active_item),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Patient name and ID
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = appointment.patientName,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxHeight(1f)
                )


            }


        }

        HorizontalDivider(
            thickness = 1.dp,
            color = colorResource(R.color.gray),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Row (
            modifier = Modifier.padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar",
                    tint = colorResource(id = R.color.gray),
                    modifier = Modifier.padding(end = 10.dp)
                )
                Column {
                    Text(
                        text = appointment.date.format(DateTimeFormatter.ofPattern("EE, MMMM d yyyy")),
                        fontWeight = FontWeight.Medium,
                        color = colorResource(id = R.color.gray)
                    )
                    Text(
                        text = "${appointment.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                                appointment.endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        color = Color.Gray
                    )

                }
            }

            Icon(
                imageVector = appointment.state.icon,
                contentDescription = appointment.state.name,
                tint = appointment.state.iconColor
            )



        }
    }
}



@Composable
fun PatientNoteCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Patient Note",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.black)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_blue).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (appointment.patientNote.isNotEmpty()) appointment.patientNote else "No notes provided",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp),
                    color = Color.DarkGray
                )
            }
        }
    }
}