package com.example.doctorek.ui.screens.doctorScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.ui.viewmodels.AppointmentsViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullAppointmentsScreen(navController: NavController, viewModel: AppointmentsViewModel = viewModel()) {
    // State for filters
    var searchQuery by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusMonths(1)) }
    var selectedState by remember { mutableStateOf<AppointmentState?>(null) }

    // State for dropdown menus
    var showStateDropdown by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val state = viewModel.uiState.collectAsState()

    if (state.value.loading){
        // Show loading indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Loading appointments...", color = Color.Gray)
        }

    } else {

        val appointments = state.value.appointments
        Log.d("Appointments", "Appointments: $appointments")
        // Filter appointments based on criteria

        val filteredAppointments = appointments.filter { appointment ->
            val matchesSearch = if (searchQuery.isBlank()) true else
                appointment.patientName.contains(searchQuery, ignoreCase = true)

            val matchesDateRange = appointment.date in startDate..endDate

            val matchesState = if (selectedState == null) true else
                appointment.state == selectedState

            matchesSearch && matchesDateRange && matchesState
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Appointments",
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = colorResource(id = R.color.nav_bar_active_item)
                    )
                )
            },
            containerColor = colorResource(R.color.white)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Filters section
                FiltersSection(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    startDate = startDate,
                    onStartDateClick = { showStartDatePicker = true },
                    endDate = endDate,
                    onEndDateClick = { showEndDatePicker = true },
                    selectedState = selectedState,
                    onStateClick = { showStateDropdown = !showStateDropdown },
                    showStateDropdown = showStateDropdown,
                    onStateSelected = {
                        selectedState = it
                        showStateDropdown = false
                    },
                    onClearState = {
                        selectedState = null
                        showStateDropdown = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Appointments list
                if (filteredAppointments.isEmpty()) {
                    EmptyAppointmentsMessage()
                } else {
                    AppointmentsList(
                        appointments = filteredAppointments,
                        onAppointmentClick = { appointment ->
                            navController?.navigate("appointment_details/${appointment.id}")
                        }
                    )
                }

                // Start Date Picker Dialog
                if (showStartDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showStartDatePicker = false },
                        onDateSelected = {
                            startDate = it
                            showStartDatePicker = false
                            // Ensure start date is not after end date
                            if (startDate.isAfter(endDate)) {
                                endDate = startDate
                            }
                        },
                        initialDate = startDate,
                        title = "Select Start Date"
                    )
                }

                // End Date Picker Dialog
                if (showEndDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showEndDatePicker = false },
                        onDateSelected = {
                            endDate = it
                            showEndDatePicker = false
                            // Ensure end date is not before start date
                            if (endDate.isBefore(startDate)) {
                                startDate = endDate
                            }
                        },
                        initialDate = endDate,
                        title = "Select End Date",
                        minDate = startDate // End date can't be before start date
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate,
    title: String,
    minDate: LocalDate? = null
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 24 * 60 * 60 * 1000,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (minDate == null) return true

                val localDate = LocalDate.ofEpochDay(utcTimeMillis / (24 * 60 * 60 * 1000))
                return !localDate.isBefore(minDate)
            }
        }
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val days = millis / (24 * 60 * 60 * 1000)
                        onDateSelected(LocalDate.ofEpochDay(days))
                    } ?: onDismissRequest()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colorResource(id = R.color.nav_bar_active_item)
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = { Text(text = title) },
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = colorResource(id = R.color.nav_bar_active_item),
                todayContentColor = colorResource(id = R.color.nav_bar_active_item),
                todayDateBorderColor = colorResource(id = R.color.nav_bar_active_item)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    startDate: LocalDate,
    onStartDateClick: () -> Unit,
    endDate: LocalDate,
    onEndDateClick: () -> Unit,
    selectedState: AppointmentState?,
    onStateClick: () -> Unit,
    showStateDropdown: Boolean,
    onStateSelected: (AppointmentState) -> Unit,
    onClearState: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search by patient name") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colorResource(id = R.color.nav_bar_active_item)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorResource(id = R.color.nav_bar_active_item),
                cursorColor = colorResource(id = R.color.nav_bar_active_item)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Date range and status filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Start date filter
            DateFilterButton(
                date = startDate,
                label = "From",
                onClick = onStartDateClick,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // End date filter
            DateFilterButton(
                date = endDate,
                label = "To",
                onClick = onEndDateClick,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Status filter
            Box(modifier = Modifier.weight(1f)) {
                StatusFilterButton(
                    selectedState = selectedState,
                    onClick = onStateClick
                )

                DropdownMenu(
                    expanded = showStateDropdown,
                    onDismissRequest = { onStateClick() },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("All States") },
                        onClick = { onClearState() }
                    )

                    AppointmentState.values().forEach { state ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = state.icon,
                                        contentDescription = null,
                                        tint = state.iconColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(state.name)
                                }
                            },
                            onClick = { onStateSelected(state) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateFilterButton(
    date: LocalDate,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_blue).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colorResource(id = R.color.nav_bar_active_item),
                    modifier = Modifier.size(16.dp)
                )

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MM/dd/yy")),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(id = R.color.nav_bar_active_item),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = colorResource(id = R.color.nav_bar_active_item)
            )
        }
    }
}

@Composable
fun StatusFilterButton(
    selectedState: AppointmentState?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_blue).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Text(
                    text = selectedState?.name ?: "All",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = selectedState?.iconColor ?: colorResource(id = R.color.nav_bar_active_item),
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = colorResource(id = R.color.nav_bar_active_item)
            )
        }
    }
}

@Composable
fun AppointmentsList(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                onClick = { onAppointmentClick(appointment) }
            )
        }
    }
}

@Composable
fun EmptyAppointmentsMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No appointments match your filters",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}