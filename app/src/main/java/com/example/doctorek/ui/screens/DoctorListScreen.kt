package com.example.doctorek.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.doctorek.R
import com.example.doctorek.data.models.DoctorResponse
import com.example.doctorek.ui.components.DoctorekAppBar
import com.example.doctorek.ui.viewmodels.DoctorListState
import com.example.doctorek.ui.viewmodels.DoctorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    navController: NavController,
    initialCategoryFilter: String?,
    doctorViewModel: DoctorViewModel = viewModel()
) {
    val doctorListState by doctorViewModel.doctorListState.collectAsState()
    val uniqueSpecialties by doctorViewModel.uniqueSpecialties.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Filter states
    var selectedSpecialtiesFilter by remember { mutableStateOf(setOf<String>()) }
    var minRatingFilter by remember { mutableFloatStateOf(0f) }

    // Category row state
    val categoriesForRow = remember(uniqueSpecialties) { listOf("All") + uniqueSpecialties }

    // Apply initial category filter from navigation
    LaunchedEffect(initialCategoryFilter) {
        if (!initialCategoryFilter.isNullOrEmpty()) {
            selectedSpecialtiesFilter = setOf(initialCategoryFilter)
        }
    }

    // Determine current category for row selection based on selectedSpecialtiesFilter
    val currentCategoryInRow = remember(selectedSpecialtiesFilter) {
        if (selectedSpecialtiesFilter.size == 1 && categoriesForRow.contains(selectedSpecialtiesFilter.first())) {
            selectedSpecialtiesFilter.first()
        } else {
            "All"
        }
    }

    val filteredDoctors by remember(doctorListState, searchQuery, selectedSpecialtiesFilter, minRatingFilter) {
        derivedStateOf {
            doctorListState.doctors.filter { doctor ->
                val matchesSearch = searchQuery.isEmpty() ||
                        (doctor.profiles.full_name?.contains(searchQuery, ignoreCase = true) == true) ||
                        doctor.specialty.contains(searchQuery, ignoreCase = true) ||
                        doctor.hospital_name.contains(searchQuery, ignoreCase = true)

                val matchesSpecialty = selectedSpecialtiesFilter.isEmpty() ||
                        selectedSpecialtiesFilter.contains(doctor.specialty)

                val matchesRating = doctor.average_rating >= minRatingFilter

                matchesSearch && matchesSpecialty && matchesRating
            }.sortedByDescending { it.average_rating }
        }
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
            DoctorekAppBar(
                title = "All Doctors",
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
                    IconButton(onClick = {
                        scope.launch {
                            showFilterBottomSheet = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = colorResource(id = R.color.nav_bar_active_item)
                        )
                    }
                }
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(54.dp),
                placeholder = { Text("Search doctor, specialty, hospital...") },
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
                    focusedBorderColor = colorResource(id = R.color.nav_bar_active_item),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // Categories Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoriesForRow) { category ->
                    val isSelected = category == currentCategoryInRow
                    Button(
                        onClick = {
                            if (category == "All") {
                                selectedSpecialtiesFilter = emptySet()
                            } else {
                                selectedSpecialtiesFilter = setOf(category)
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                colorResource(id = R.color.nav_bar_active_item)
                            else
                                Color.White,
                            contentColor = if (isSelected)
                                Color.White
                            else
                                colorResource(id = R.color.gray)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 2.dp else 0.dp),
                        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                    ) {
                        Text(category, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            if (doctorListState.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (doctorListState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${doctorListState.error}", color = Color.Red, textAlign = TextAlign.Center)
                }
            } else if (filteredDoctors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No doctors found matching your criteria.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                ) {
                    items(filteredDoctors, key = { it.id }) { doctor ->
                        DoctorListItem(doctor = doctor, onClick = {
                            // navController.navigate(Screens.DoctorDetails.createRoute(doctor.id)) // TODO: Add DoctorDetails screen
                        })
                    }
                }
            }
        }

        if (showFilterBottomSheet) {
            DoctorListFilterBottomSheet(
                sheetState = bottomSheetState,
                allSpecialties = uniqueSpecialties,
                currentSelectedSpecialties = selectedSpecialtiesFilter,
                currentMinRating = minRatingFilter,
                onDismiss = {
                    scope.launch {
                        bottomSheetState.hide()
                        showFilterBottomSheet = false
                    }
                },
                onApplyFilters = { newSpecialties, newRating ->
                    selectedSpecialtiesFilter = newSpecialties
                    minRatingFilter = newRating
                    scope.launch {
                        bottomSheetState.hide()
                        showFilterBottomSheet = false
                    }
                },
                onResetFilters = {
                    selectedSpecialtiesFilter = emptySet()
                    minRatingFilter = 0f
                }
            )
        }
    }
}

@Composable
fun DoctorListItem(doctor: DoctorResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = doctor.profiles.avatar_url,
                contentDescription = doctor.profiles.full_name ?: "Doctor Avatar",
                placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                error = painterResource(id = R.drawable.ic_profile_placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEDF3FF))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dr. ${doctor.profiles.full_name ?: "N/A"}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doctor.specialty,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = doctor.hospital_name,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", doctor.average_rating),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListFilterBottomSheet(
    sheetState: SheetState, 
    allSpecialties: List<String>,
    currentSelectedSpecialties: Set<String>,
    currentMinRating: Float,
    onDismiss: () -> Unit,
    onApplyFilters: (Set<String>, Float) -> Unit,
    onResetFilters: () -> Unit
) {
    var tempSelectedSpecialties by remember(currentSelectedSpecialties) { mutableStateOf(currentSelectedSpecialties) }
    var tempMinRating by remember(currentMinRating) { mutableFloatStateOf(currentMinRating) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.LightGray, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter Doctors", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF5F5F5))
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.DarkGray)
                }
            }
            Divider(modifier = Modifier.padding(bottom = 16.dp), color = Color(0xFFEEEEEE))

            // Specialties Section
            Text("Specialty", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                allSpecialties.sorted().forEach { specialty ->
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
                            colors = CheckboxDefaults.colors(checkedColor = colorResource(id = R.color.nav_bar_active_item))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(specialty, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Min Rating Section
            Text("Minimum Rating", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = tempMinRating,
                    onValueChange = { tempMinRating = it },
                    valueRange = 0f..5f,
                    steps = 9,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = colorResource(id = R.color.nav_bar_active_item),
                        activeTrackColor = colorResource(id = R.color.nav_bar_active_item).copy(alpha = 0.5f),
                        inactiveTrackColor = Color(0xFFE0E0E0)
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(String.format("%.1f", tempMinRating), style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempSelectedSpecialties = emptySet()
                        tempMinRating = 0f
                        onResetFilters()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(id = R.color.nav_bar_active_item)),
                    border = BorderStroke(1.dp, colorResource(id = R.color.nav_bar_active_item))
                ) { Text("Reset") }

                Button(
                    onClick = { onApplyFilters(tempSelectedSpecialties, tempMinRating) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.nav_bar_active_item))
                ) { Text("Apply") }
            }
        }
    }
}
