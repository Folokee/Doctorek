package com.example.doctorek.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.ui.components.DoctorekAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val appointments = remember { getAppointments() }
    val categories = listOf("All", "Brain", "Cardio", "Eye")
    var selectedCategory by remember { mutableStateOf("All") }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var qrDialogAppointment by remember { mutableStateOf<Appointment?>(null) }
    var detailsDialogAppointment by remember { mutableStateOf<Appointment?>(null) }

    // Filter state
    var filterState by remember { mutableStateOf("All") }
    var filterDoctor by remember { mutableStateOf("") }
    var filterDate by remember { mutableStateOf("") }

    Scaffold(
        containerColor = colorResource(id = R.color.white),
        topBar = {
            Column {
                DoctorekAppBar(
                    title = "My Appointments",
                    actions = {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = colorResource(id = R.color.nav_bar_active_item)
                            )
                        }
                    }
                )
                Divider(color = colorResource(id = R.color.light_gray), thickness = 1.dp)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Categories Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    Button(
                        onClick = { selectedCategory = category },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategory == category)
                                colorResource(id = R.color.blue)
                            else
                                colorResource(id = R.color.white),
                            contentColor = if (selectedCategory == category)
                                colorResource(id = R.color.white)
                            else
                                colorResource(id = R.color.gray)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(category)
                    }
                }
            }

            // Appointments List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var filteredAppointments = if (selectedCategory == "All") appointments else appointments.filter { it.category == selectedCategory }
                // Apply filter dialog filters
                filteredAppointments = filteredAppointments.filter { appt ->
                    (filterState == "All" || appt.state == filterState) &&
                    (filterDoctor.isBlank() || appt.doctorName.contains(filterDoctor, ignoreCase = true)) &&
                    (filterDate.isBlank() || appt.date.contains(filterDate))
                }
                items(filteredAppointments) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onQrClick = { qrDialogAppointment = appointment },
                        onCardClick = { detailsDialogAppointment = appointment }
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = colorResource(id = R.color.white),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(colorResource(id = R.color.light_gray), RoundedCornerShape(4.dp))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    "Filter Appointments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Filter by State
                Text("Status", fontWeight = FontWeight.Medium, color = colorResource(id = R.color.black))
                Spacer(modifier = Modifier.height(8.dp))
                val states = listOf("All", "Confirmed", "Pending", "Completed", "Cancelled")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(states) { state ->
                        OutlinedButton(
                            onClick = { filterState = state },
                            shape = RoundedCornerShape(20.dp),
                            border = if (filterState == state) ButtonDefaults.outlinedButtonBorder else null,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (filterState == state)
                                    colorResource(id = R.color.blue)
                                else
                                    colorResource(id = R.color.white),
                                contentColor = if (filterState == state)
                                    colorResource(id = R.color.white)
                                else
                                    colorResource(id = R.color.gray)
                            )
                        ) {
                            Text(state)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filter by Doctor Name
                Text("Doctor Name", fontWeight = FontWeight.Medium, color = colorResource(id = R.color.black))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = filterDoctor,
                    onValueChange = { filterDoctor = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter doctor name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filter by Date (simple string match)
                Text("Date", fontWeight = FontWeight.Medium, color = colorResource(id = R.color.black))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = filterDate,
                    onValueChange = { filterDate = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("YYYY-MM-DD") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue))
                ) {
                    Text("Apply", color = colorResource(id = R.color.white))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        filterState = "All"
                        filterDoctor = ""
                        filterDate = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(id = R.color.blue)
                    )
                ) {
                    Text("Reset")
                }
            }
        }
    }

    // QR Code Dialog
    if (qrDialogAppointment != null) {
        Dialog(
            onDismissRequest = { qrDialogAppointment = null },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .widthIn(min = 260.dp, max = 320.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Placeholder for QR code
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(colorResource(id = R.color.light_gray), shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Replace with your QR code composable if available
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Code",
                            tint = colorResource(id = R.color.blue),
                            modifier = Modifier.size(120.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Scan the QR code to confirm the patient’s arrival and updates the appointment status in\nreal-time.",
                        color = colorResource(id = R.color.black),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { qrDialogAppointment = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue))
                    ) {
                        Text("Close", color = colorResource(id = R.color.white))
                    }
                }
            }
        }
    }

    // Appointment Details Dialog
    if (detailsDialogAppointment != null) {
        Dialog(
            onDismissRequest = { detailsDialogAppointment = null },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .widthIn(min = 260.dp, max = 340.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "Appointment Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.black)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val appt = detailsDialogAppointment!!
                    InfoRow("Doctor", appt.doctorName)
                    InfoRow("Specialty", appt.specialty)
                    InfoRow("Category", appt.category)
                    InfoRow("Date", appt.date)
                    InfoRow("Status", appt.state)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { detailsDialogAppointment = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue))
                    ) {
                        Text("Close", color = colorResource(id = R.color.white))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            label,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = colorResource(id = R.color.gray)
        )
        Text(
            value,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = colorResource(id = R.color.black)
        )
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onQrClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // State Icon
            AppointmentStateIcon(state = appointment.state)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    appointment.doctorName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.black)
                )
                Text(
                    appointment.specialty,
                    color = colorResource(id = R.color.gray),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = "Date",
                        tint = colorResource(id = R.color.gray),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        appointment.date,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.gray)
                    )
                }
            }

            IconButton(
                onClick = {
                    onQrClick()
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(colorResource(id = R.color.light_blue), shape = CircleShape)
            ) {
                Icon(Icons.Default.QrCode, "QR Code", tint = colorResource(id = R.color.black))
            }
        }
    }
}

@Composable
fun AppointmentStateIcon(state: String) {
    val (icon, color) = when (state) {
        "Confirmed" -> Icons.Default.CheckCircle to colorResource(id = R.color.bottle_green)
        "Pending" -> Icons.Default.HourglassEmpty to colorResource(id = R.color.pink)
        "Completed" -> Icons.Default.DoneAll to colorResource(id = R.color.blue)
        "Cancelled" -> Icons.Default.Cancel to colorResource(id = R.color.pink)
        else -> Icons.Default.Event to colorResource(id = R.color.gray)
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(color.copy(alpha = 0.12f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = state,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
    }
}

data class Appointment(
    val id: Int,
    val doctorName: String,
    val specialty: String,
    val state: String,
    val category: String,
    val date: String
)

fun getAppointments(): List<Appointment> {
    return listOf(
        Appointment(1, "Dr. Eleanor Pena", "Cardio specialist", "Confirmed", "Cardio", "2024-06-10 09:00"),
        Appointment(2, "Dr. Eleanor Pena", "Cardio specialist", "Pending", "Brain", "2024-06-12 14:30"),
        Appointment(3, "Dr. Eleanor Pena", "Cardio specialist", "Completed", "Eye", "2024-05-30 11:00"),
        Appointment(4, "Dr. Eleanor Pena", "Cardio specialist", "Cancelled", "Cardio", "2024-06-01 16:00")
    )
}