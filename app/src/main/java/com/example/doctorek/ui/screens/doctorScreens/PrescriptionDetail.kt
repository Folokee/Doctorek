package com.example.doctorek.ui.screens.doctorScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.ui.viewmodels.PrescriptionsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionDetailScreen(
    prescriptionId: String? = null,
    navController: NavController,
    viewModel: PrescriptionsViewModel = viewModel(),
) {
    // Determine if this is add mode or view mode
    val isAddMode = prescriptionId == "new" || prescriptionId == null

    // State for form inputs
    val patientName = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(LocalDate.now().toString()) }
    val medications = remember { mutableStateListOf<Medication>() }

    val state = viewModel.uiState.collectAsState()

    // Load existing prescription data if in view mode
    LaunchedEffect(prescriptionId) {
        if (!isAddMode) {
            state.value.prescriptions.find { it.id == prescriptionId }?.let { prescription ->
                patientName.value = prescription.patientName
                date.value = prescription.date.toString()

                // Load medications
                medications.clear()
                prescription.medications.forEach { med ->
                    medications.add(Medication(med.name, med.instructions))
                }
            }
        } else {
            // Initialize with empty data for add mode
            patientName.value = ""
            date.value = LocalDate.now().toString()
            medications.clear()
            // Add one empty medication by default in add mode
            medications.add(Medication("", ""))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isAddMode) "New Prescription" else "Prescription Details",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = colorResource(R.color.white)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Information Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Patient Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = patientName.value,
                        onValueChange = { if (isAddMode) patientName.value = it },
                        label = { Text("Patient Name") },
                        readOnly = !isAddMode,
                        enabled = isAddMode,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(id = R.color.nav_bar_active_item),
                            focusedLabelColor = colorResource(id = R.color.nav_bar_active_item),
                            cursorColor = colorResource(id = R.color.nav_bar_active_item),
                            disabledBorderColor = Color.Gray,
                            disabledTextColor = Color.Black
                        )
                    )

                    OutlinedTextField(
                        value = if (isAddMode) date.value
                        else LocalDate.parse(date.value).format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                        onValueChange = { /* Date is not editable */ },
                        label = { Text("Date") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.Gray,
                            disabledTextColor = Color.Black
                        )
                    )
                }
            }

            // Medications Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Medications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (isAddMode) {
                            IconButton(onClick = { medications.add(Medication("", "")) }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Medication",
                                    tint = colorResource(id = R.color.nav_bar_active_item)
                                )
                            }
                        }
                    }

                    if (medications.isEmpty()) {
                        Text(
                            text = if (isAddMode) "Add medications using the + button above"
                            else "No medications prescribed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        medications.forEachIndexed { index, medication ->
                            MedicationItemCard(
                                medication = medication,
                                isEditable = isAddMode,
                                onNameChange = { if (isAddMode) medications[index] = medication.copy(name = it) },
                                onInstructionsChange = { if (isAddMode) medications[index] = medication.copy(instructions = it) },
                                onDelete = { if (isAddMode && medications.size > 1) medications.removeAt(index) }
                            )
                        }
                    }
                }
            }

            // Action Button - Only show in add mode
            if (isAddMode) {
                Button(
                    onClick = {
                        // Create and save the new prescription
                        val newPrescription = Prescription(
                            id = System.currentTimeMillis().toString(),
                            patientName = patientName.value,
                            date = LocalDate.now(),
                            medications = medications.filter { it.name.isNotEmpty() }
                        )

                        viewModel.savePrescription(
                            null,

                        )

                        // Here you would call a method to save the prescription
                        // For now, just navigate back
                    },
                    enabled = patientName.value.isNotBlank() && medications.any { it.name.isNotBlank() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.nav_bar_active_item),
                        disabledContainerColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save"
                        )
                        Text(text = "Save Prescription")
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationItemCard(
    medication: Medication,
    isEditable: Boolean,
    onNameChange: (String) -> Unit,
    onInstructionsChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_blue).copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medication",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                if (isEditable) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Medication",
                            tint = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = medication.name,
                onValueChange = onNameChange,
                label = { Text("Name") },
                readOnly = !isEditable,
                enabled = isEditable,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.nav_bar_active_item),
                    focusedLabelColor = colorResource(id = R.color.nav_bar_active_item),
                    cursorColor = colorResource(id = R.color.nav_bar_active_item),
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = medication.instructions,
                onValueChange = onInstructionsChange,
                label = { Text("Instructions") },
                readOnly = !isEditable,
                enabled = isEditable,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.nav_bar_active_item),
                    focusedLabelColor = colorResource(id = R.color.nav_bar_active_item),
                    cursorColor = colorResource(id = R.color.nav_bar_active_item),
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black
                )
            )
        }
    }
}