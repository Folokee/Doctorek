package com.example.doctorek.ui.screens.appointementScreens
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import android.content.Intent
import android.net.Uri
import com.example.doctorek.R
import com.example.doctorek.data.models.ContactInformation
import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.repositories.DoctorRepository
import com.example.doctorek.ui.screens.state.AvailabilityState
import com.example.doctorek.ui.screens.state.DoctorDetailState
import com.example.doctorek.ui.viewmodels.DoctorDetailViewModel

// Define the Source Sans Pro font family
val sourceSansPro = FontFamily(
    Font(R.font.sourcesans3_regular,FontWeight.Normal),
    Font(R.font.sourcesans3_semibold, FontWeight.SemiBold),
    Font(R.font.sourcesans3_extrabold, FontWeight.Bold)
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctorId: String,
    onBackClick: () -> Unit,
    onBookAppointment: (String, String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: DoctorDetailViewModel = viewModel(
        factory = DoctorDetailViewModel.Companion.Factory(context)
    )
    
    // Collect UI state from ViewModel
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val isFavorite by viewModel.favoriteState.collectAsStateWithLifecycle()
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsStateWithLifecycle()

    // Load doctor details when the screen is first displayed
    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (uiState) {
                        is DoctorDetailState.Success -> (uiState as DoctorDetailState.Success).doctor.profiles.full_name
                        else -> "Doctor Details"
                    }?.let {
                        Text(
                            text = it,
                            fontFamily = sourceSansPro,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3E7BFA)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F8FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color(0xFF3E7BFA)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues -> 
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is DoctorDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DoctorDetailState.Error -> {
                    val errorMessage = (uiState as DoctorDetailState.Error).message
                    ErrorView(
                        message = errorMessage,
                        onRetry = { viewModel.loadDoctorDetails(doctorId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is DoctorDetailState.Success -> {
                    val doctor = (uiState as DoctorDetailState.Success).doctor
                    DoctorDetailContent(
                        doctorDetail = doctor,
                        isFavorite = isFavorite,
                        selectedDayIndex = selectedDayIndex,
                        onDaySelected = { index -> viewModel.selectDay(index) },
                        onBookAppointment = onBookAppointment
                    )
                }
            }
        }
    }
}


@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorDetailContent(
    doctorDetail: DoctorDetailResponse,
    isFavorite: Boolean,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    onBookAppointment: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Doctor profile section
        DoctorProfileSection(doctorDetail = doctorDetail, fontFamily = sourceSansPro)

        Spacer(modifier = Modifier.height(24.dp))
        // Stats section
        DoctorStatsSection(doctorDetail = doctorDetail, fontFamily = sourceSansPro)

        Spacer(modifier = Modifier.height(24.dp))

        // Social media buttons (now handles null safely)
        SocialMediaSection(doctorDetail.contact_information)

        Spacer(modifier = Modifier.height(24.dp))

        // About doctor section
        AboutDoctorSection(doctorDetail = doctorDetail, fontFamily = sourceSansPro)

        Spacer(modifier = Modifier.height(24.dp))

        // Working time section
        WorkingTimeSection(doctorDetail = doctorDetail, fontFamily = sourceSansPro)

        Spacer(modifier = Modifier.height(24.dp))

        // Hospital/Clinic section
        HospitalSection(doctorDetail = doctorDetail, fontFamily = sourceSansPro)

        Spacer(modifier = Modifier.height(32.dp))

        // Appointment section
        Text(
            text = "Make Appointment",
            fontFamily = sourceSansPro,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DateSelectionSection(
            doctorId = doctorDetail.id,
            selectedDayIndex = selectedDayIndex,
            onDaySelected = onDaySelected,
            fontFamily = sourceSansPro,
            onBookAppointment = onBookAppointment
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DoctorProfileSection(doctorDetail: DoctorDetailResponse, fontFamily: FontFamily) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Profile Image
        doctorDetail.profiles.avatar_url?.let { avatarUrl -> 
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Doctor Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.doctor),
                placeholder = painterResource(id = R.drawable.doctor)
            )
        } ?: Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = "Doctor Profile",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = doctorDetail.profiles.full_name ?: "Dr. Unknown",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = doctorDetail.specialty,
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF7E8299),
            fontSize = 16.sp
        )
    }
}
@Composable
fun DoctorStatsSection(doctorDetail: DoctorDetailResponse, fontFamily: FontFamily) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            iconRes = R.drawable.patients,
            value = "1000+", // Using placeholder as this might not be in the API
            label = "Patients",
            backgroundColor = Color(0xFFF0F8FF),
            iconTint = Color(0xFF3E7BFA),
            fontFamily = fontFamily
        )

        StatItem(
            iconRes = R.drawable.experience,
            value = "${doctorDetail.years_of_experience} Yrs",
            label = "Experience",
            backgroundColor = Color(0xFFFFF0F5),
            iconTint = Color(0xFFF63E7B),
            fontFamily = fontFamily
        )

        StatItem(
            iconRes = R.drawable.stars,
            value = doctorDetail.average_rating.toString(),
            label = "Ratings",
            backgroundColor = Color(0xFFFFFAF0),
            iconTint = Color(0xFFF7A646),
            fontFamily = fontFamily
        )
    }
}

@Composable
fun StatItem(
    iconRes: Int,
    value: String,
    label: String,
    backgroundColor: Color,
    iconTint: Color,
    fontFamily: FontFamily
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(95.dp)
            .height(95.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(vertical = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Text(
            text = label,
            fontFamily = fontFamily,
            color = Color(0xFF7E8299),
            fontSize = 14.sp
        )
    }
}

@Composable
fun SocialMediaSection(contactInfo: ContactInformation?) {
    val context = LocalContext.current
    
    if (contactInfo == null) {
        // Return early if contactInfo is null
        return
    }
    
    // Function to safely check if a link is valid and not empty
    fun isValidLink(url: String?): Boolean {
        return url != null && url.isNotBlank()
    }
    
    // Function to open URLs
    fun openUrl(url: String?) {
        if (url != null && url.isNotBlank()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback for when no app can handle the URL
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(browserIntent)
            }
        }
    }
    
    // Check if at least one social media link exists
    val hasFacebook = isValidLink(contactInfo.facebook_link)
    val hasWhatsapp = isValidLink(contactInfo.whatsapp_link)
    val hasLinkedIn = isValidLink(contactInfo.linkedin_link)
    
    // If no social media links, don't show the section at all
    if (!hasFacebook && !hasWhatsapp && !hasLinkedIn) {
        return
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Facebook (clickable if link exists)
        if (hasFacebook) {
            Box(
                modifier = Modifier
                    .clickable { openUrl(contactInfo.facebook_link) }
            ) {
                CustomImage(R.drawable.facebook, 40.dp)
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        
        // WhatsApp (clickable if link exists)
        if (hasWhatsapp) {
            Box(
                modifier = Modifier
                    .clickable { openUrl(contactInfo.whatsapp_link) }
            ) {
                CustomImage(R.drawable.whatsapp, 40.dp)
            }
            Spacer(modifier = Modifier.width(20.dp))
        }

        // LinkedIn (clickable if link exists)
        if (hasLinkedIn) {
            Box(
                modifier = Modifier
                    .clickable { openUrl(contactInfo.linkedin_link) }
            ) {
                CustomImage(R.drawable.linkedin, 40.dp)
            }
        }
    }
}

@Composable
fun SocialButton(
    iconRes: Int,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}


@Composable
fun AboutDoctorSection(doctorDetail: DoctorDetailResponse, fontFamily: FontFamily) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "About Doctor",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = doctorDetail.bio,
            fontFamily = fontFamily,
            color = Color(0xFF7E8299),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        
        // Only show contact information if it's available
        doctorDetail.contact_information?.let { contactInfo ->
            Spacer(modifier = Modifier.height(16.dp))

            // Only show phone if available
            contactInfo.phone?.let { phone ->
                Text(
                    text = "Phone number: $phone",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Only show email if available
            contactInfo.email?.let { email ->
                Text(
                    text = "Email: $email",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun WorkingTimeSection(doctorDetail: DoctorDetailResponse, fontFamily: FontFamily) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Working time",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Safely get office hours, show "Not available" if office_hours is null
        Text(
            text = doctorDetail.contact_information?.office_hours ?: "Not available",
            fontFamily = fontFamily,
            color = Color(0xFF7E8299),
            fontSize = 14.sp
        )
    }
}

@Composable
fun HospitalSection(doctorDetail: DoctorDetailResponse, fontFamily: FontFamily) {
    val context = LocalContext.current
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hospital/Clinic Name",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = doctorDetail.hospital_name,
            fontFamily = fontFamily,
            color = Color(0xFF7E8299),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Address: ${doctorDetail.hospital_address}",
            fontFamily = fontFamily,
            color = Color(0xFF7E8299),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Map location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { 
                // Create a more reliable Google Maps intent
                try {
                    // Format: "google.navigation:q=lat,lng"
                    val lat = doctorDetail.location_lat
                    val lng = doctorDetail.location_lng
                    val hospitalName = doctorDetail.hospital_name.replace(" ", "+")
                    
                    // First try Google Maps specific URI format
                    val gmmIntentUri = Uri.parse("geo:0,0?q=$lat,$lng($hospitalName)")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        // Fall back to web URL if Google Maps app isn't installed
                        val mapsUrl = "https://www.google.com/maps/search/?api=1&query=$lat,$lng"
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))
                        context.startActivity(browserIntent)
                    }
                } catch (e: Exception) {
                    // Last resort - open browser with Google Maps URL
                    val mapUrl = "https://maps.google.com/?q=${doctorDetail.location_lat},${doctorDetail.location_lng}"
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                    context.startActivity(webIntent)
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = null,
                tint = Color(0xFF3E7BFA),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Click Here to open Map Location",
                fontFamily = fontFamily,
                color = Color(0xFF3E7BFA),
                fontSize = 14.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelectionSection(
    doctorId: String,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    fontFamily: FontFamily = sourceSansPro,
    onBookAppointment: (String, String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: DoctorDetailViewModel = viewModel(
        factory = DoctorDetailViewModel.Companion.Factory(context)
    )
    
    // Get current date and generate all days of the week
    val today = LocalDate.now()
    
    // Generate all 7 days of the week starting from today
    val weekDays = remember {
        generateWeekDays(today)
    }
    
    // Get availability state from ViewModel
    val availabilityState by viewModel.availabilityState.collectAsStateWithLifecycle()
    
    // Parse office hours from doctor
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val officeHoursText = when (uiState) {
        is com.example.doctorek.ui.screens.state.DoctorDetailState.Success -> {
            (uiState as com.example.doctorek.ui.screens.state.DoctorDetailState.Success)
                .doctor.contact_information?.office_hours ?: ""
        }
        else -> ""
    }
    
    // Parse workdays from office hours (e.g., "Monday-Friday 9AM-5PM")
    val workdays = remember(officeHoursText) {
        parseWorkdays(officeHoursText)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Days of week row - scrollable if needed
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weekDays.size) { index ->
                    val dayInfo = weekDays[index]
                    
                    // Check if this day is available based on both:
                    // 1. The doctor's office hours (workdays)
                    // 2. The doctor's availability from API (availabilityState)
                    val isDayAvailable = when (availabilityState) {
                        is AvailabilityState.Available -> {
                            val availableDaysList = (availabilityState as AvailabilityState.Available).availableDays
                            dayInfo.originalDate.dayOfWeek in availableDaysList && 
                            dayInfo.originalDate.dayOfWeek.name.lowercase().capitalize() in workdays
                        }
                        else -> dayInfo.originalDate.dayOfWeek.name.lowercase().capitalize() in workdays // Fall back to office hours only
                    }
                    
                    DayOfWeekItem(
                        day = dayInfo,
                        isSelected = selectedDayIndex == index, // Use selectedDayIndex directly here
                        isAvailable = isDayAvailable,
                        onClick = { onDaySelected(index) },
                        fontFamily = fontFamily
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Show availability message based on state
        when (availabilityState) {
            is AvailabilityState.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF3E7BFA)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Checking doctor's availability...",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            is AvailabilityState.NotAvailable -> {
                Text(
                    text = "This doctor has no available slots currently. Please contact them directly.",
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    color = Color(0xFFF63E7B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            is AvailabilityState.Error -> {
                Text(
                    text = "Couldn't verify availability. You may still try booking.",
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            is AvailabilityState.Available -> {
                // If specific day selected is not available
                val selectedDayInfo = weekDays[selectedDayIndex]
                val availableDaysList = (availabilityState as AvailabilityState.Available).availableDays
                
                if (selectedDayInfo.originalDate.dayOfWeek !in availableDaysList) {
                    Text(
                        text = "Doctor is not available on ${selectedDayInfo.day}. Please select another day.",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFFF63E7B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Doctor is available on this day!",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Book appointment button logic
        val isBookingEnabled = when (availabilityState) {
            is AvailabilityState.Available -> {
                val selectedDayInfo = weekDays[selectedDayIndex]
                val availableDaysList = (availabilityState as AvailabilityState.Available).availableDays
                val isDayWorkday = selectedDayInfo.originalDate.dayOfWeek.name.lowercase().capitalize() in workdays
                selectedDayInfo.originalDate.dayOfWeek in availableDaysList && isDayWorkday
            }
            else -> false
        }
        
        Button(
            onClick = {
                val selectedDayInfo = weekDays[selectedDayIndex]
                val selectedDate = selectedDayInfo.originalDate
                onBookAppointment(doctorId, selectedDate.toString())
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            enabled = isBookingEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3E7BFA),
                disabledContainerColor = Color(0xFFBDBDBD)
            )
        ) {
            Text(
                "Book Appointment",
                fontFamily = sourceSansPro,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isBookingEnabled) Color.White else Color.DarkGray
            )
        }
    }
}

// Generate list of all 7 days starting from today
@RequiresApi(Build.VERSION_CODES.O)
fun generateWeekDays(startDate: LocalDate): List<DayInfo> {
    val days = mutableListOf<DayInfo>()
    var currentDate = startDate
    
    // Add 7 days of the week
    repeat(7) {
        val dayOfWeek = currentDate.dayOfWeek.getDisplayName(
            TextStyle.SHORT, Locale.getDefault()
        ).uppercase()
        
        days.add(
            DayInfo(
                date = currentDate.dayOfMonth,
                day = dayOfWeek,
                originalDate = currentDate
            )
        )
        
        currentDate = currentDate.plusDays(1)
    }
    
    return days
}

// Parse office hours to extract workdays
@RequiresApi(Build.VERSION_CODES.O)
fun parseWorkdays(officeHours: String?): List<String> {
    if (officeHours.isNullOrBlank()) return emptyList()
    
    // Handle common formats like "Monday-Friday 9AM-5PM"
    val allDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    
    return try {
        val parts = officeHours.split(" ")
        val daysPart = parts.firstOrNull() ?: return emptyList()
        
        if (daysPart.contains("-")) {
            // Range of days (e.g., "Monday-Friday")
            val range = daysPart.split("-")
            if (range.size == 2) {
                val startDay = range[0].capitalize()
                val endDay = range[1].capitalize()
                
                val startIndex = allDays.indexOf(startDay)
                val endIndex = allDays.indexOf(endDay)
                
                if (startIndex >= 0 && endIndex >= 0 && startIndex <= endIndex) {
                    allDays.subList(startIndex, endIndex + 1)
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        } else {
            // Individual days separated by commas
            daysPart.split(",").map { it.trim().capitalize() }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// New component for days of the week
@Composable
fun DayOfWeekItem(
    day: DayInfo,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    val backgroundColor = when {
        !isAvailable -> Color(0xFFF5F5F5)
        isSelected -> Color(0xFF3E7BFA) // Blue background for selected day
        else -> Color.White
    }
    
    val textColor = when {
        !isAvailable -> Color.Gray
        isSelected -> Color.White // White text on blue background
        else -> Color.Black
    }
    
    val dayColor = when {
        !isAvailable -> Color.Gray
        isSelected -> Color.White.copy(alpha = 0.8f)
        else -> Color.Gray
    }
    
    val borderColor = when {
        !isAvailable -> Color.LightGray
        isSelected -> Color(0xFF3E7BFA)
        else -> Color.LightGray
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp) // Smaller width for 7 days
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isAvailable, onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = day.day,
            fontFamily = fontFamily,
            fontSize = 14.sp,
            color = dayColor
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = day.date.toString(),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = textColor
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Always show a status label - either "Available" or "Unavailable"
        if (!isAvailable) {
            Text(
                text = "Unavailable",
                fontFamily = fontFamily,
                fontSize = 9.sp,
                color = Color(0xFFF63E7B)
            )
        } else {
            Text(
                text = "Available",
                fontFamily = fontFamily,
                fontSize = 9.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF4CAF50)
            )
        }
    }
}

data class DayInfo(
    val date: Int, 
    val day: String,
    val originalDate: LocalDate
)

@Composable
fun CustomImage(
    @DrawableRes imageResId: Int,
    size: Dp
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(size)
    )
}