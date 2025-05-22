package com.example.doctorek.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.data.models.PatientAppointment
import com.example.doctorek.ui.components.DoctorekAppBar
import com.example.doctorek.ui.viewmodels.AppointmentFilter
import com.example.doctorek.ui.viewmodels.PatientAppointmentViewModel
import java.util.Locale
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState

private fun decodeQrCodeBase64(dataUrl: String?): Bitmap? {
    if (dataUrl.isNullOrEmpty()) return null

    try {
        val base64Data = dataUrl.substringAfter("base64,")
        val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AppointmentsScreen(navController: NavController) {
    val viewModel: PatientAppointmentViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uniqueSpecialties by viewModel.uniqueSpecialties.collectAsStateWithLifecycle()
    val selectedSpecialties by viewModel.selectedSpecialties.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val categories = listOf("All", "Scheduled", "Confirmed", "Completed", "Cancelled")
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var qrDialogAppointment by remember { mutableStateOf<PatientAppointment?>(null) }
    var detailsDialogAppointment by remember { mutableStateOf<PatientAppointment?>(null) }
    val scrollState = rememberLazyListState()

    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    var selectedCategory by remember { mutableStateOf("All") }

    var searchText by remember { mutableStateOf("") }

    // Pull-to-refresh state
    val isRefreshing = state.loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshAppointments() }
    )

    val blueColor = colorResource(id = R.color.blue)
    val greenColor = colorResource(id = R.color.bottle_green)
    val lightBlueColor = colorResource(id = R.color.light_blue)

    val filteredAppointments = state.filteredAppointments

    val onFilterSelected: (String) -> Unit = { filter ->
        selectedCategory = filter
        when (filter) {
            "All" -> {
                selectedFilters = emptySet()
                viewModel.setFilter(AppointmentFilter.ALL)
            }
            "Confirmed" -> {
                selectedFilters = setOf("confirmed")
                viewModel.setFilter(AppointmentFilter.CONFIRMED)
            }
            "Scheduled" -> {
                selectedFilters = setOf("scheduled")
                viewModel.setFilter(AppointmentFilter.SCHEDULED)
            }
            "Completed" -> {
                selectedFilters = setOf("completed")
                viewModel.setFilter(AppointmentFilter.COMPLETED)
            }
            "Cancelled" -> {
                selectedFilters = setOf("cancelled")
                viewModel.setFilter(AppointmentFilter.CANCELLED)
            }
            else -> {
                selectedFilters = emptySet()
                viewModel.setFilter(AppointmentFilter.ALL)
            }
        }
    }

    val onSearch: (String) -> Unit = { query ->
        searchText = query
        viewModel.searchAppointments(query)
    }

    var tempSelectedSpecialties by remember(selectedSpecialties) { mutableStateOf(selectedSpecialties) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                DoctorekAppBar(
                    title = "My Appointments",
                    actions = {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = blueColor
                            )
                        }
                    }
                )
                Divider(color = colorResource(id = R.color.light_gray), thickness = 1.dp)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        blueColor,
                                        blueColor.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    "Manage Your Appointments",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Schedule and track your upcoming visits",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 2
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        onSearch(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),
                    placeholder = { Text("Search appointments") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = blueColor
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = blueColor,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = category == selectedCategory
                            Button(
                                onClick = { onFilterSelected(category) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected)
                                        blueColor
                                    else
                                        Color.White,
                                    contentColor = if (isSelected)
                                        Color.White
                                    else
                                        colorResource(id = R.color.gray)
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 2.dp else 0.dp),
                                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null
                            ) {
                                Text(
                                    category,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    if (state.loading && !isRefreshing) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = blueColor)
                        }
                    } else if (state.error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Error: ${state.error}",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.fetchPatientAppointments() },
                                    colors = ButtonDefaults.buttonColors(containerColor = blueColor)
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filteredAppointments) { appointment ->
                                PatientAppointmentCard(
                                    appointment = appointment,
                                    onCardClick = { detailsDialogAppointment = appointment },
                                    onQrClick = { qrDialogAppointment = appointment }
                                )
                            }

                            if (filteredAppointments.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(120.dp)
                                                .background(blueColor.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Event,
                                                contentDescription = "No appointments",
                                                tint = blueColor,
                                                modifier = Modifier.size(80.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "No appointments found",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF2E3A59)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Schedule a new appointment or try different search terms",
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 32.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (state.filteredAppointments.isNotEmpty() && selectedCategory != "All") {
                            Text(
                                text = "Showing ${state.filteredAppointments.size} ${selectedCategory.lowercase()} appointments",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                color = colorResource(id = R.color.gray),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = Color.White,
                contentColor = blueColor
            )
        }
    }

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
                        .padding(vertical = 8.dp)
                        .width(40.dp)
                        .background(colorResource(id = R.color.light_gray), RoundedCornerShape(4.dp))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Filter by Specialty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black)
                    )

                    IconButton(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.DarkGray
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFEEEEEE))

                Text(
                    "Doctor Specialty", 
                    fontWeight = FontWeight.Medium, 
                    color = colorResource(id = R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    uniqueSpecialties.forEach { specialty ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempSelectedSpecialties = if (tempSelectedSpecialties.contains(specialty)) {
                                        tempSelectedSpecialties - specialty
                                    } else {
                                        tempSelectedSpecialties + specialty
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = tempSelectedSpecialties.contains(specialty),
                                onCheckedChange = {
                                    tempSelectedSpecialties = if (it) {
                                        tempSelectedSpecialties + specialty
                                    } else {
                                        tempSelectedSpecialties - specialty
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colorResource(id = R.color.blue)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(specialty, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    
                    if (uniqueSpecialties.isEmpty()) {
                        Text(
                            "No specialties found",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Doctor Name", fontWeight = FontWeight.Medium, color = colorResource(id = R.color.black))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        onSearch(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter doctor name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetFilters()
                            selectedCategory = "All"
                            tempSelectedSpecialties = emptySet()
                            searchText = ""
                            showFilterSheet = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorResource(id = R.color.blue)
                        ),
                        border = BorderStroke(1.dp, colorResource(id = R.color.blue))
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = { 
                            viewModel.updateSpecialtyFilters(tempSelectedSpecialties)
                            showFilterSheet = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.blue)
                        )
                    ) {
                        Text("Apply", color = colorResource(id = R.color.white))
                    }
                }
            }
        }
    }

    if (qrDialogAppointment != null) {
        Dialog(
            onDismissRequest = { qrDialogAppointment = null },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .widthIn(min = 260.dp, max = 320.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val qrCodeBitmap = remember(qrDialogAppointment) {
                        qrDialogAppointment?.qr_code?.let { decodeQrCodeBase64(it) }
                    }
                    
                    if (qrCodeBitmap != null) {
                        Image(
                            bitmap = qrCodeBitmap.asImageBitmap(),
                            contentDescription = "Appointment QR Code",
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(blueColor.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = blueColor,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Scan the QR code to confirm the patient's arrival and update the appointment status in real-time.",
                        color = colorResource(id = R.color.black),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { qrDialogAppointment = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blueColor)
                    ) {
                        Text("Close", color = colorResource(id = R.color.white))
                    }
                }
            }
        }
    }

    if (detailsDialogAppointment != null) {
        Dialog(
            onDismissRequest = { detailsDialogAppointment = null },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
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

                    InfoRow("Doctor ID", appt.doctor_id)
                    if (appt.doctor_info != null) {
                        InfoRow("Doctor Name", appt.doctor_info.full_name)
                        InfoRow("Specialty", appt.doctor_info.speciality)
                        InfoRow("Hospital", appt.doctor_info.hospital_name)
                    }
                    InfoRow("Date", "${appt.appointment_date} at ${appt.appointment_time}")
                    InfoRow("Status", capitalizeStatus(appt.status))
                    if (appt.reason != null) {
                        InfoRow("Reason", appt.reason)
                    }
                    if (appt.notes != null && appt.notes.isNotBlank()) {
                        InfoRow("Notes", appt.notes)
                    }

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

fun capitalizeStatus(status: String): String {
    return status.replaceFirstChar { 
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            label,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = colorResource(id = R.color.gray)
        )
        Text(
            value ?: "Not available",
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = colorResource(id = R.color.black)
        )
    }
}

@Composable
fun PatientAppointmentCard(
    appointment: PatientAppointment,
    onQrClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val statusColor = getStatusColor(appointment.status)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = statusColor.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = capitalizeStatus(appointment.status),
                        color = statusColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onQrClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(colorResource(id = R.color.light_blue), shape = CircleShape),
                ) {
                    Icon(Icons.Default.QrCode, "QR Code", tint = colorResource(id = R.color.black))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppointmentStateIcon(state = appointment.status)

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.doctor_info?.full_name ?: "Doctor #${appointment.doctor_id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.black)
                    )

                    Text(
                        text = appointment.doctor_info?.speciality ?: (appointment.reason ?: "Appointment"),
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
                            "${appointment.appointment_date} at ${appointment.appointment_time}",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.gray)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "confirmed" -> colorResource(id = R.color.bottle_green)
        "scheduled", "pending" -> colorResource(id = R.color.blue)
        "completed" -> colorResource(id = R.color.bottle_green)
        "cancelled" -> colorResource(id = R.color.pink)
        else -> colorResource(id = R.color.gray)
    }
}

@Composable
fun AppointmentStateIcon(state: String) {
    val (icon, color) = when (state.lowercase()) {
        "confirmed" -> Icons.Default.CheckCircle to colorResource(id = R.color.bottle_green)
        "scheduled", "pending" -> Icons.Default.HourglassEmpty to colorResource(id = R.color.blue)
        "completed" -> Icons.Default.DoneAll to colorResource(id = R.color.blue)
        "cancelled" -> Icons.Default.Cancel to colorResource(id = R.color.pink)
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