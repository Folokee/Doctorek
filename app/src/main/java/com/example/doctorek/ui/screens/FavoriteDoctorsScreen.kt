package com.example.doctorek.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.ui.components.DoctorekAppBar
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDoctorsScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedDoctor by remember { mutableStateOf<FavoriteDoctor?>(null) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // State for filters
    var minRating by remember { mutableFloatStateOf(3.0f) }
    val specialtyOptions = remember { listOf("Virologist", "Oncologists", "Surgeon", "Podiatriciah", "Rheumatologists", "Dermatologist", "Cardio specialist") }
    val selectedSpecialties = remember { mutableStateListOf<String>() }

    // Main doctor list and filtered list
    val allDoctors = remember { getFavoriteDoctors().toMutableList() }
    val filteredDoctors by remember {
        derivedStateOf {
            allDoctors.filter { doctor ->
                val matchesSearch = doctor.name.contains(searchQuery, ignoreCase = true) ||
                        doctor.specialty.contains(searchQuery, ignoreCase = true)
                val matchesRating = doctor.rating >= minRating
                val matchesSpecialty = selectedSpecialties.isEmpty() ||
                        selectedSpecialties.contains(doctor.specialty)
                matchesSearch && matchesRating && matchesSpecialty
            }
        }
    }

    val onResetFilters: () -> Unit = {
        minRating = 3.0f
        selectedSpecialties.clear()
    }

    val onMinRatingChange: (Float) -> Unit = {
        minRating = it
    }

    val onSpecialtyToggle: (String) -> Unit = { specialty ->
        if (selectedSpecialties.contains(specialty)) {
            selectedSpecialties.remove(specialty)
        } else {
            selectedSpecialties.add(specialty)
        }
    }

    val onApplyFilters: () -> Unit = {
        showFilterSheet = false
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                DoctorekAppBar(
                    title = "Favorite Doctors",
                    modifier = Modifier.padding(top = 24.dp),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showFilterSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = colorResource(id = R.color.nav_bar_active_item)
                            )
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    placeholder = {
                        Text(
                            "Search for a doctor",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredDoctors.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No doctors match your criteria",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    items(filteredDoctors) { doctor ->
                        FavoriteDoctorItem(
                            doctor = doctor,
                            onClick = { },
                            onFavoriteClick = {
                                selectedDoctor = doctor
                                showRemoveDialog = true
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showRemoveDialog && selectedDoctor != null) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            containerColor = Color.White,
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Remove from favorites?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Are you sure you want to remove ${selectedDoctor?.name} from your favorites?",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDoctor?.let { doctorToRemove ->
                            allDoctors.remove(doctorToRemove)
                            showRemoveDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.nav_bar_active_item)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Yes, Remove")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRemoveDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(id = R.color.nav_bar_active_item)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showFilterSheet) {
        Dialog(onDismissRequest = { showFilterSheet = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter Doctors",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        IconButton(
                            onClick = { showFilterSheet = false },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F5F5))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = Color.DarkGray
                            )
                        }
                    }

                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                    // Filter content
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                            .fillMaxWidth()
                    ) {
                        // Min Rating Section
                        Text(
                            text = "Minimum Rating",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Slider(
                                value = minRating,
                                onValueChange = onMinRatingChange,
                                valueRange = 0f..5f,
                                steps = 4,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = colorResource(id = R.color.nav_bar_active_item),
                                    activeTrackColor = colorResource(id = R.color.nav_bar_active_item).copy(alpha = 0.5f),
                                    inactiveTrackColor = Color(0xFFE0E0E0)
                                ),
                                thumb = {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(colorResource(id = R.color.nav_bar_active_item)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF5F5F5),
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(36.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = minRating.toString(),
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Specialties Section
                        Text(
                            text = "Specialty",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            specialtyOptions.forEach { specialty ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedSpecialties.contains(specialty))
                                        colorResource(id = R.color.nav_bar_active_item).copy(alpha = 0.1f)
                                    else
                                        Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSpecialtyToggle(specialty) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                                    ) {
                                        Checkbox(
                                            checked = selectedSpecialties.contains(specialty),
                                            onCheckedChange = { onSpecialtyToggle(specialty) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = colorResource(id = R.color.nav_bar_active_item),
                                                uncheckedColor = Color(0xFFBDBDBD)
                                            )
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = specialty,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (selectedSpecialties.contains(specialty))
                                                    FontWeight.Medium else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onResetFilters,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorResource(id = R.color.nav_bar_active_item)
                                )
                            ) {
                                Text("Reset")
                            }

                            Button(
                                onClick = onApplyFilters,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.nav_bar_active_item)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                            ) {
                                Text("Apply")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteDoctorItem(
    doctor: FavoriteDoctor,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEDF3FF)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = doctor.imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = doctor.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = doctor.specialty,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFF8E6))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⭐ ${doctor.rating}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFF9500)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "(${doctor.reviewCount} reviews)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    )

                    if (doctor.location.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = doctor.location,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF4FF))
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = colorResource(id = R.color.nav_bar_active_item),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

data class FavoriteDoctor(
    val id: Int,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewCount: Int,
    val imageResId: Int,
    val location: String = "",
    val showHighlight: Boolean = false
)

fun getFavoriteDoctors(): List<FavoriteDoctor> {
    return listOf(
        FavoriteDoctor(1, "Dr. Bellamy N", "Virologist", 4.5, 150, R.drawable.doctor_1),
        FavoriteDoctor(2, "Dr. Mensah T", "Oncologists", 4.3, 129, R.drawable.doctor_2),
        FavoriteDoctor(3, "Dr. Klimisch J", "Surgeon", 4.6, 120, R.drawable.doctor_1),
        FavoriteDoctor(4, "Dr. Martinez K", "Podiatriciah", 4.7, 101, R.drawable.doctor_2),
        FavoriteDoctor(5, "Dr. Marc M", "Rheumatologists", 4.3, 130, R.drawable.doctor_1),
        FavoriteDoctor(6, "Dr. O'Boyle J", "Dermatologist", 4.9, 105, R.drawable.doctor_2),
        FavoriteDoctor(7, "Dr. Eleanor Pena", "Cardio specialist", 4.8, 120, R.drawable.doctor_1, "Great Hospital", true)
    )
}