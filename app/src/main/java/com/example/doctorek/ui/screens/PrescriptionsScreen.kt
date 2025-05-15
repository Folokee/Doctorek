package com.example.doctorek.ui.screens

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
fun PrescriptionsScreen(navController: NavController) {
    val prescriptions = remember { getPrescriptions() }
    var detailsDialogPrescription by remember { mutableStateOf<Prescription?>(null) }
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    
    // Animation state for header
    val scrollOffset = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
    val isScrolled = scrollOffset.value > 0
    
    // Filter prescriptions based on search
    val filteredPrescriptions = remember(searchText) {
        prescriptions.filter { 
            searchText.isEmpty() || 
            it.doctorName.contains(searchText, ignoreCase = true) || 
            it.description.contains(searchText, ignoreCase = true)
        }
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
                                text = "${filteredPrescriptions.size}",
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
                
                // List of prescriptions with violet as primary color
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPrescriptions) { prescription ->
                        NewPrescriptionCard(
                            prescription = prescription,
                            onDownloadClick = { 
                                coroutineScope.launch {
                                    // TODO: Implement download logic
                                }
                            },
                            onCardClick = { detailsDialogPrescription = prescription },
                            primaryColor = violetColor,
                            secondaryColor = lavenderColor
                        )
                    }
                    
                    // Empty state with more cohesive colors
                    if (filteredPrescriptions.isEmpty()) {
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

    // Update dialog colors to be more cohesive
    if (detailsDialogPrescription != null) {
        NewPrescriptionDetailsDialog(
            prescription = detailsDialogPrescription!!,
            onDismiss = { detailsDialogPrescription = null },
            violetColor = violetColor,
            lavenderColor = lavenderColor
        )
    }
}

@Composable
fun NewPrescriptionCard(
    prescription: Prescription,
    onDownloadClick: () -> Unit,
    onCardClick: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color
) {
    // Single cohesive color for all cards
    val cardColor = primaryColor
    
    Card(
        shape = RoundedCornerShape(20.dp), // More rounded than appointments
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
                    val doctorInitials = prescription.doctorName.split(" ")
                        .take(2)
                        .joinToString("") { it.first().toString() }
                        
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        cardColor,
                                        cardColor.copy(alpha = 0.7f)
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
                            prescription.doctorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2E3A59)
                        )
                        
                        Text(
                            prescription.date,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                
                // Prescription number as a pill-shaped tag
                Text(
                    text = "#${prescription.id}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = cardColor,
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
                            .background(cardColor.copy(alpha = 0.3f), CircleShape)
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
                            color = cardColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = cardColor,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(-45f)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Description with colorful text
                Text(
                    prescription.description,
                    fontSize = 14.sp,
                    color = Color(0xFF2E3A59),
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Download button with more cohesive gradient
            Button(
                onClick = onDownloadClick,
                shape = RoundedCornerShape(16.dp), // More rounded than appointments
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

@Composable
fun NewPrescriptionDetailsDialog(
    prescription: Prescription,
    onDismiss: () -> Unit,
    violetColor: Color,
    lavenderColor: Color
) {
    // Use main violet color for dialog
    val dialogColor = violetColor
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(24.dp), // More rounded for visual distinction
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
                                colors = listOf(dialogColor, dialogColor.copy(alpha = 0.7f))
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
                
                // Prescription title
                Text(
                    "Prescription #${prescription.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = dialogColor
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Doctor info with custom layout
                PrescriptionInfoRow(
                    icon = Icons.Default.Person,
                    label = "Prescribed by",
                    value = prescription.doctorName,
                    color = dialogColor
                )
                
                PrescriptionInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Date Issued",
                    value = prescription.date,
                    color = dialogColor
                )
                
                // Description with decorative elements
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
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = dialogColor,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            "Instructions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = dialogColor
                        )
                    }
                    
                    // Prescription box with decorative border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = dialogColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = dialogColor.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            prescription.description,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = Color(0xFF2E3A59)
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
                        border = BorderStroke(1.dp, dialogColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = dialogColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Close",
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Button(
                        onClick = { /* TODO: Download function */ },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = dialogColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
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

// Data models (unchanged)
data class Prescription(
    val id: Int,
    val doctorName: String,
    val date: String,
    val description: String
)

fun getPrescriptions(): List<Prescription> {
    return listOf(
        Prescription(1, "Dr. Eleanor Pena", "2024-06-10", "Take 1 tablet of Paracetamol every 8 hours for 5 days."),
        Prescription(2, "Dr. John Smith", "2024-06-12", "Apply eye drops twice daily for 7 days."),
        Prescription(3, "Dr. Alice Brown", "2024-05-30", "Complete the full course of antibiotics as prescribed."),
        Prescription(4, "Dr. Mark Lee", "2024-06-01", "Rest and hydrate. Return for follow-up in 1 week.")
    )
}