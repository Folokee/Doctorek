package com.example.doctorek.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.data.models.Medication
import com.example.doctorek.data.models.PatientPrescription
import com.example.doctorek.ui.components.DoctorekAppBar
import com.example.doctorek.ui.viewmodels.PrescriptionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: PrescriptionViewModel = viewModel(factory = PrescriptionViewModel.Factory(context))
    val uiState by viewModel.uiState.collectAsState()
    
    var detailsDialogPrescription by remember { mutableStateOf<PatientPrescription?>(null) }
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    
    // Track when search text changes
    LaunchedEffect(searchText) {
        viewModel.searchPrescriptions(searchText)
    }
    
    // Track PDF Uri
    LaunchedEffect(uiState.pdfUri) {
        uiState.pdfUri?.let { uri ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            try {
                context.startActivity(intent)
                viewModel.clearPdfUri()
            } catch (e: Exception) {
                Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    // Animation state for header
    val scrollOffset = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
    val isScrolled = scrollOffset.value > 0
    
    // Get prescriptions to display (either filtered or all)
    val prescriptionsToDisplay = if (uiState.filteredPrescriptions.isNotEmpty()) {
        uiState.filteredPrescriptions
    } else {
        uiState.prescriptions
    }

    // Refined color palette with harmonious colors
    val violetColor = Color(0xFF6B4BFF) // Primary violet color
    val lavenderColor = Color(0xFFAB9BFF) // Secondary - softer violet
    val paleVioletColor = Color(0xFFE0DBFF) // Tertiary - very light violet for backgrounds
    
    // Subtle background color with violet tint
    val backgroundColor = Color(0xFFF9F7FF) // Very light violet background

    Scaffold(
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                DoctorekAppBar(
                    title = "My Prescriptions",
                    actions = {
                        IconButton(onClick = { /* TODO: Add filter options */ }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = violetColor
                            )
                        }
                    }
                )
                Divider(color = violetColor.copy(alpha = 0.2f), thickness = 1.dp)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = violetColor)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header banner with more cohesive gradient
                    if (!isScrolled) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                violetColor,
                                                lavenderColor // Softer violet for gradient
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
                                            "Your Medications",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "All your prescriptions in one place",
                                            fontSize = 14.sp,
                                            color = Color.White.copy(alpha = 0.9f),
                                            maxLines = 2
                                        )
                                    }
                                    
                                    // Medicine icon in a diamond shape
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .rotate(45f)
                                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .rotate(-45f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search Bar with violet styling
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(56.dp),
                        placeholder = { Text("Find a prescription") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = violetColor
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
                        shape = RoundedCornerShape(28.dp), // More rounded for distinction
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = violetColor,
                            unfocusedBorderColor = violetColor.copy(alpha = 0.3f),
                            cursorColor = violetColor
                        )
                    )

                    // Prescription count with more cohesive colors
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Diamond-shaped counter with more cohesive gradient
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .rotate(45f)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(violetColor, lavenderColor)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${prescriptionsToDisplay.size}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.rotate(-45f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = "Active Prescriptions",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E3A59)
                                )
                            )
                        }
                        
                        // Sort button with more cohesive colors
                        TextButton(
                            onClick = { /* TODO: Sort function */ },
                            colors = ButtonDefaults.textButtonColors(contentColor = violetColor)
                        ) {
                            Text(
                                "Sort by date",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Sort options"
                            )
                        }
                    }
                    
                    // Error message
                    if (uiState.error != null && !uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: ${uiState.error}",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    // List of prescriptions with violet as primary color
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(prescriptionsToDisplay) { prescription ->
                            PrescriptionCard(
                                prescription = prescription,
                                onDownloadClick = { 
                                    viewModel.downloadPrescriptionPdf(prescription)
                                },
                                onCardClick = { detailsDialogPrescription = prescription },
                                primaryColor = violetColor,
                                secondaryColor = lavenderColor,
                                isDownloading = uiState.isDownloading
                            )
                        }
                        
                        // Empty state with more cohesive colors
                        if (prescriptionsToDisplay.isEmpty() && !uiState.isLoading) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Colorful empty state with more cohesive colors
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(paleVioletColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MedicalServices,
                                            contentDescription = "No prescriptions",
                                            tint = violetColor,
                                            modifier = Modifier.size(70.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Text(
                                        text = if (searchText.isEmpty()) 
                                            "No prescriptions yet!" 
                                        else 
                                            "No matching prescriptions",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = violetColor
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = if (searchText.isEmpty())
                                            "Your prescriptions will appear here"
                                        else
                                            "Try different search terms",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Show prescription details dialog
    if (detailsDialogPrescription != null) {
        PrescriptionDetailsDialog(
            prescription = detailsDialogPrescription!!,
            onDismiss = { detailsDialogPrescription = null },
            onDownload = { viewModel.downloadPrescriptionPdf(detailsDialogPrescription!!) },
            violetColor = violetColor,
            lavenderColor = lavenderColor,
            isDownloading = uiState.isDownloading
        )
    }
}

@Composable
fun PrescriptionCard(
    prescription: PatientPrescription,
    onDownloadClick: () -> Unit,
    onCardClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color,
    isDownloading: Boolean
) {
    // Format the date to be more readable
    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(prescription.prescriptionDate)
        outputFormat.format(date)
    } catch (e: Exception) {
        prescription.prescriptionDate
    }
    
    // Get a simplified description
    val medicationDescription = prescription.details.medications.joinToString(", ") { it.name }
    
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top section with circular avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Doctor info with colorful avatar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Doctor initials in a circular avatar
                    val doctorInitials = if (prescription.doctor.name.isBlank()) {
                        "DR" // Default initials when name is empty
                    } else {
                        prescription.doctor.name.split(" ")
                            .filter { it.isNotEmpty() }
                            .take(2)
                            .joinToString("") { it.firstOrNull()?.toString() ?: "" }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        primaryColor,
                                        primaryColor.copy(alpha = 0.7f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = doctorInitials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            prescription.doctor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2E3A59)
                        )
                        
                        Text(
                            formattedDate,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                
                // Prescription ID as a pill-shaped tag
                val shortId = prescription.localId.substringAfter("-").take(8)
                Text(
                    text = "#$shortId",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = primaryColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            
            // Patterned divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(vertical = 8.dp)
            ) {
                // Decorative dotted line
                repeat(40) { index ->
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(primaryColor.copy(alpha = 0.3f), CircleShape)
                            .align(Alignment.CenterStart)
                            .offset(x = (index * 8).dp)
                    )
                }
            }
            
            // Prescription content
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Pill icon for prescription
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(45f)
                        .background(
                            color = primaryColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(-45f)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Description with colorful text
                Text(
                    medicationDescription,
                    fontSize = 14.sp,
                    color = Color(0xFF2E3A59),
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Download button with progress indicator
            Button(
                onClick = { if (!isDownloading) onDownloadClick() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(primaryColor, secondaryColor)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Generating PDF...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download PDF",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Download PDF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PrescriptionDetailsDialog(
    prescription: PatientPrescription,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    violetColor: Color,
    lavenderColor: Color,
    isDownloading: Boolean
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header icon with diamond shape
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .rotate(45f)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(violetColor, violetColor.copy(alpha = 0.7f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .rotate(-45f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Format prescription date
                val formattedDate = try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                    val date = inputFormat.parse(prescription.prescriptionDate)
                    outputFormat.format(date)
                } catch (e: Exception) {
                    prescription.prescriptionDate
                }
                
                // Prescription title
                Text(
                    "Prescription #${prescription.localId.substring(3, 11)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = violetColor
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Doctor info with custom layout
                PrescriptionInfoRow(
                    icon = Icons.Default.Person,
                    label = "Prescribed by",
                    value = if (prescription.doctor.name.isBlank()) "Doctor" else prescription.doctor.name,
                    color = violetColor
                )
                
                PrescriptionInfoRow(
                    icon = Icons.Default.LocalHospital,
                    label = "Hospital",
                    value = prescription.doctor.hospitalName,
                    color = violetColor
                )
                
                PrescriptionInfoRow(
                    icon = Icons.Default.MedicalServices,
                    label = "Specialty",
                    value = prescription.doctor.specialty,
                    color = violetColor
                )
                
                PrescriptionInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Date Issued",
                    value = formattedDate,
                    color = violetColor
                )
                
                // Medications section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = violetColor,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            "Medications",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = violetColor
                        )
                    }
                    
                    // List of medications
                    prescription.details.medications.forEachIndexed { index, medication ->
                        MedicationItem(
                            medication = medication,
                            index = index,
                            violetColor = violetColor
                        )
                    }
                }
                
                // Additional notes if available
                if (!prescription.additionalNotes.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null,
                                tint = violetColor,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                "Additional Notes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = violetColor
                            )
                        }
                        
                        Text(
                            prescription.additionalNotes,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF2E3A59),
                            modifier = Modifier.padding(start = 28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Custom buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, violetColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = violetColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Close",
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Button(
                        onClick = { if (!isDownloading) onDownload() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = violetColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Generating...",
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Download",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationItem(
    medication: Medication,
    index: Int,
    violetColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, bottom = 12.dp)
    ) {
        Text(
            text = "${index + 1}. ${medication.name}",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E3A59)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp)
        ) {
            MedicationDetail(
                label = "Dosage",
                value = medication.dosage,
                color = violetColor,
                modifier = Modifier.weight(1f)
            )
            
            MedicationDetail(
                label = "Frequency",
                value = medication.frequency,
                color = violetColor,
                modifier = Modifier.weight(1f)
            )
        }
        
        MedicationDetail(
            label = "Duration",
            value = medication.duration,
            color = violetColor,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

@Composable
fun MedicationDetail(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF2E3A59)
        )
    }
}

@Composable
fun PrescriptionInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E3A59)
            )
        }
    }
}