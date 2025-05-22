package com.example.doctorek.ui.screens.doctorScreens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.ui.components.DoctorekAppBar
import com.example.doctorek.ui.viewmodels.PrescriptionsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class Prescription(
    val id : String,
    val patientName : String,
    val date: LocalDate,
    val medications: List<Medication>,
    )
data class Medication(
    val name: String,
    val instructions : String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionsScreen(
    navController: NavController,
    viewModel : PrescriptionsViewModel = viewModel(),
) {
    val context = LocalContext.current
    val sharedPrefs = SharedPrefs(context)
    val my_name = sharedPrefs.getName()
    val state = viewModel.uiState.collectAsState()

    if (state.value.loading) {
        // Show loading indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        var searchQuery by remember { mutableStateOf("") }
        var isLatestFirst by remember { mutableStateOf(true) }
        val prescriptions = remember { state.value.prescriptions }

        val filteredPrescriptions = prescriptions.filter {
            if (searchQuery.isBlank()) true
            else it.patientName.contains(searchQuery, ignoreCase = true) ||
                    it.medications.any { med -> med.name.contains(searchQuery, ignoreCase = true) }
        }.let {
            if (isLatestFirst) it.sortedByDescending { p -> p.date }
            else it.sortedBy { p -> p.date }
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("prescriptions_detail/new") },
                    containerColor = colorResource(id = R.color.nav_bar_active_item),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Prescription")
                }
            },
            topBar = {
                DoctorekAppBar(my_name ?: "Dr. Smith")
            },
            containerColor = colorResource(R.color.white)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Filters bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sort button
                    SortButton(
                        isLatestFirst = isLatestFirst,
                        onSortChanged = { isLatestFirst = it }
                    )

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search") },
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
                            .weight(1f)
                            .height(56.dp)
                    )
                }

                // Prescriptions list
                if (filteredPrescriptions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No prescriptions found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredPrescriptions) { prescription ->
                            PrescriptionCard(
                                prescription = prescription,
                                onClick = {
                                    navController.navigate("prescriptions_detail/${prescription.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortButton(
    isLatestFirst: Boolean,
    onSortChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .height(56.dp)
            .clickable { onSortChanged(!isLatestFirst) },
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_blue).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sort",
                tint = colorResource(id = R.color.nav_bar_active_item)
            )

            Text(
                text = if (isLatestFirst) "Latest" else "Oldest",
                color = colorResource(id = R.color.nav_bar_active_item),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Patient name and medications
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = prescription.patientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.black)
                )
                Row {
                    prescription.medications.take(3).forEach { medication ->
                        Text(
                            text = "${medication.name}, ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (prescription.medications.size > 3) {
                        Text(
                            text = "+ ${prescription.medications.size - 3} more",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorResource(id = R.color.nav_bar_active_item),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }




            }

            // Right side: Date
            Text(
                text = prescription.date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// Sample data
fun getSamplePrescriptions(): List<Prescription> {
    return listOf(
        Prescription(
            id = "1",
            patientName = "John Doe",
            date = LocalDate.now(),
            medications = listOf(
                Medication("Amoxicillin 500mg", "Take 1 tablet 3 times daily with meals"),
                Medication("Ibuprofen 400mg", "Take 1 tablet every 6 hours as needed for pain")
            )
        ),
        Prescription(
            id = "2",
            patientName = "Jane Smith",
            date = LocalDate.now().minusDays(2),
            medications = listOf(
                Medication("Loratadine 10mg", "Take 1 tablet daily"),
                Medication("Fluticasone Nasal Spray", "Use 1 spray in each nostril once daily"),
                Medication("Benzocaine Throat Lozenges", "Dissolve 1 lozenge in mouth every 2 hours as needed")
            )
        ),
        Prescription(
            id = "3",
            patientName = "Robert Johnson",
            date = LocalDate.now().minusDays(5),
            medications = listOf(
                Medication("Metformin 500mg", "Take 1 tablet twice daily with meals"),
                Medication("Lisinopril 10mg", "Take 1 tablet daily in the morning")
            )
        ),
        Prescription(
            id = "4",
            patientName = "Emily Davis",
            date = LocalDate.now().minusWeeks(2),
            medications = listOf(
                Medication("Atorvastatin 20mg", "Take 1 tablet daily at bedtime"),
                Medication("Aspirin 81mg", "Take 1 tablet daily with food"),
                Medication("Metoprolol 25mg", "Take 1 tablet twice daily")
            )
        ),
        Prescription(
            id = "5",
            patientName = "Michael Brown",
            date = LocalDate.now().minusWeeks(1),
            medications = listOf(
                Medication("Prednisone 10mg", "Take 2 tablets daily for 5 days, then 1 tablet daily for 5 days"),
                Medication("Albuterol Inhaler", "Use 2 puffs every 4-6 hours as needed")
            )
        ),
        Prescription(
            id = "6",
            patientName = "Sarah Williams",
            date = LocalDate.now().minusDays(1),
            medications = listOf(
                Medication("Ciprofloxacin 500mg", "Take 1 tablet twice daily for 7 days"),
                Medication("Probiotics", "Take 1 capsule daily")
            )
        ),
        Prescription(
            id = "7",
            patientName = "David Miller",
            date = LocalDate.now().minusMonths(1),
            medications = listOf(
                Medication("Sertraline 50mg", "Take 1 tablet daily in the morning"),
                Medication("Melatonin 3mg", "Take 1 tablet 30 minutes before bedtime as needed"),
                Medication("Vitamin D3 2000 IU", "Take 1 tablet daily with food"),
                Medication("Omega-3 Fish Oil 1000mg", "Take 1 capsule twice daily with meals")
            )
        )
    )
}