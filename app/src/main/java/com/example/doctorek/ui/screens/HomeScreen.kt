package com.example.doctorek.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.Screens
import com.example.doctorek.ui.components.DoctorekAppBar
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.doctorek.data.models.DoctorResponse
import com.example.doctorek.ui.viewmodels.DoctorViewModel
import coil.compose.AsyncImage

val horizontalPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    doctorViewModel: DoctorViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val doctorListState by doctorViewModel.doctorListState.collectAsState()

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                DoctorekAppBar(
                    title = "Doctorek",
                    navigationIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { 
                                navController.navigate(Screens.FavoriteDoctors.route)
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorResource(id = R.color.light_blue).copy(alpha = 0.1f))
                                .padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite Doctors",
                                tint = colorResource(id = R.color.nav_bar_active_item)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorResource(id = R.color.light_blue).copy(alpha = 0.1f))
                                .padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = colorResource(id = R.color.nav_bar_active_item)
                            )
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Special Doctor",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colorResource(id = R.color.nav_bar_active_item),
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate(Screens.DoctorList.route)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 4
                ) {
                    getDoctorCategories().forEach { category ->
                        DoctorCategoryItem(category = category) {
                            navController.navigate(Screens.DoctorList.route + "?category=${category.name}")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top Doctors",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colorResource(id = R.color.nav_bar_active_item),
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate(Screens.DoctorList.route)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (doctorListState.loading) {
                        Text("Loading doctors...")
                    } else if (doctorListState.error != null) {
                        Text("Error: ${doctorListState.error}")
                    } else {
                        val topDoctors = doctorListState.doctors
                            .sortedByDescending { it.average_rating }
                        topDoctors.forEach { doctor ->
                            DoctorCard(doctor = doctor) {
                                // Handle doctor item click
                                navController.navigate("doctorDetail/${doctor.id}")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    maxItemsInEachRow: Int,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints -> 
        val rows = mutableListOf<List<Placeable>>()
        val itemWidth = constraints.maxWidth / maxItemsInEachRow

        val itemConstraints = constraints.copy(
            minWidth = 0,
            maxWidth = itemWidth
        )

        var currentRow = mutableListOf<Placeable>()
        var currentRowItemCount = 0

        measurables.forEach { measurable ->
            if (currentRowItemCount >= maxItemsInEachRow) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowItemCount = 0
            }
            currentRow.add(measurable.measure(itemConstraints))
            currentRowItemCount++
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        val height = rows.sumOf { row -> row.maxOfOrNull { it.height } ?: 0 } +
                (rows.size - 1) * 20

        layout(constraints.maxWidth, height) {
            var y = 0

            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOfOrNull { it.height } ?: 0

                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += itemWidth
                }

                y += rowHeight + 20
            }
        }
    }
}

@Composable
fun DoctorCategoryItem(
    category: DoctorCategory,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(category.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = category.iconResId),
                contentDescription = category.name,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

@Composable
fun DoctorCard(
    doctor: DoctorResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = doctor.profiles.avatar_url,
                    contentDescription = doctor.profiles.full_name ?: "Doctor Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                    error = painterResource(id = R.drawable.ic_profile_placeholder)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Dr. ${doctor.profiles.full_name ?: "N/A"}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = doctor.specialty,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", doctor.average_rating),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

data class DoctorCategory(
    val id: Int,
    val name: String,
    val iconResId: Int,
    val backgroundColor: Color
)

fun getDoctorCategories(): List<DoctorCategory> {
    return listOf(
        DoctorCategory(1, "Consultation", R.drawable.ic_consultation, Color(0xFF4285F4)),
        DoctorCategory(2, "Dental", R.drawable.ic_dental, Color(0xFF8B6DFF)),
        DoctorCategory(3, "Heart", R.drawable.ic_heart, Color(0xFFFF6D6D)),
        DoctorCategory(4, "Hospital", R.drawable.ic_hospital, Color(0xFFFFAC4B)),
        DoctorCategory(5, "Medicine", R.drawable.ic_medicine, Color(0xFF2CC09C)),
        DoctorCategory(6, "Physician", R.drawable.ic_physician, Color(0xFF4BCAFF)),
        DoctorCategory(7, "Skin", R.drawable.ic_skin, Color(0xFFFF4BCA)),
        DoctorCategory(8, "Surgeon", R.drawable.ic_surgeon, Color(0xFFFF6D4B))
    )
}