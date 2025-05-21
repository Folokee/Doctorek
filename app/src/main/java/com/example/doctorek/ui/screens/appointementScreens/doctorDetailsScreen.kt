package com.example.doctorek.ui.screens.appointementScreens
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.doctorek.R
import com.example.doctorek.data.models.ContactInformation
import com.example.doctorek.data.models.DoctorDetailResponse
import com.example.doctorek.data.repositories.DoctorRepository
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
    onBookAppointment: (String, String) -> Unit,
    viewModel: DoctorDetailViewModel = viewModel(
        factory = DoctorDetailViewModel.Factory(
            DoctorRepository(LocalContext.current)
        )
    )
) {
    // Collect UI state from ViewModel
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val isFavorite by viewModel.favoriteState.collectAsStateWithLifecycle()
    //val isFavorite = remember { mutableStateOf(false) }
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsStateWithLifecycle()

    // Load doctor details when the screen is first displayed
    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
    }
    Scaffold(
        topBar = {
            /*TopAppBar(
                title = { /* Empty title - we'll handle it in content */ },
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
                    IconButton(onClick = { isFavorite.value = !isFavorite.value }) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F8FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
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
            )*/
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
                        onBookAppointment = onBookAppointment,
                        paddingValues = paddingValues
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
    onBookAppointment: (String, String) -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Doctor profile section
        DoctorProfileSection(doctorDetail = doctorDetail, fontFamily = sourceSansPro)

        Spacer(modifier = Modifier.height(24.dp))
        // Stats section
        DoctorStatsSection(doctorDetail = doctorDetail, fontFamily = sourceSansPro)

        Spacer(modifier = Modifier.height(24.dp))

        // Social media buttons
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

/*@Composable
fun SocialMediaSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomImage(R.drawable.facebook,40.dp)

        Spacer(modifier = Modifier.width(20.dp))

        CustomImage(R.drawable.whatsapp,40.dp)

        Spacer(modifier = Modifier.width(20.dp))

        CustomImage(R.drawable.linkedin,40.dp)
    }
}
*/

@Composable
fun SocialMediaSection(contactInfo: ContactInformation) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Facebook (clickable if link exists)
        Box(
            modifier = Modifier
                .clickable(
                    enabled = contactInfo.facebook_link.isNotBlank(),
                    onClick = { /* Open Facebook link */ }
                )
        ) {
            CustomImage(R.drawable.facebook, 40.dp)
        }

        Spacer(modifier = Modifier.width(20.dp))
        // WhatsApp (clickable if link exists)
        Box(
            modifier = Modifier
                .clickable(
                    enabled = contactInfo.whatsapp_link.isNotBlank(),
                    onClick = { /* Open WhatsApp link */ }
                )
        ) {
            CustomImage(R.drawable.whatsapp, 40.dp)
        }

        Spacer(modifier = Modifier.width(20.dp))

        // LinkedIn (clickable if link exists)
        Box(
            modifier = Modifier
                .clickable(
                    enabled = contactInfo.linkedin_link.isNotBlank(),
                    onClick = { /* Open LinkedIn link */ }
                )
        ) {
            CustomImage(R.drawable.linkedin, 40.dp)
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
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Phone number: ${doctorDetail.contact_information.phone}",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Email: ${doctorDetail.contact_information.email}",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
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

        Text(
            text = doctorDetail.contact_information.office_hours,
            fontFamily = fontFamily,
            color = Color(0xFF7E8299),
            fontSize = 14.sp
        )
    }
}

@Composable
fun HospitalSection(doctorDetail: DoctorDetailResponse, fontFamily: FontFamily) {
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
        val mapLocationStr = "${doctorDetail.location_lat},${doctorDetail.location_lng}"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* Open map with coordinates */ }
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
    // Get current date and calculate next 4 available days (starting from today)
    val today = LocalDate.now()
    val selectedDay = remember { selectedDayIndex }
    val availableDays = remember {
        generateAvailableDays(today)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            availableDays.forEachIndexed { index, dayInfo ->
                DateItem(
                    day = dayInfo,
                    isSelected = selectedDay == index,
                    onClick = { onDaySelected(index) },
                    fontFamily = fontFamily
                )
            }
        }
        Button(
            onClick ={
                val selectedDay = availableDays[selectedDayIndex]
                val selectedDate = LocalDate.now()
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(selectedDay.day)))
                    .withDayOfMonth(selectedDay.date)

                onBookAppointment(doctorId, selectedDate.toString())
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3E7BFA)
            )
        ) {
            Text(
                "Book Appointment",
                fontFamily = sourceSansPro,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Generate a list of 4 available days starting from today
@RequiresApi(Build.VERSION_CODES.O)
fun generateAvailableDays(startDate: LocalDate): List<DayInfo> {
    val days = mutableListOf<DayInfo>()
    var currentDate = startDate

    // Skip to next day if current time is past clinic hours
    // This is optional - you can remove this logic if you want to include today always

    // Add 4 available days (you can adjust this number)
    repeat(4) {
        // Skip weekends if needed
        while (isWeekend(currentDate)) {
            currentDate = currentDate.plusDays(1)
        }

        val dayOfWeek = currentDate.dayOfWeek.getDisplayName(
            TextStyle.SHORT, Locale.getDefault()
        ).uppercase()

        days.add(
            DayInfo(
                date = currentDate.dayOfMonth,
                day = dayOfWeek
            )
        )

        currentDate = currentDate.plusDays(1)
    }

    return days
}

// Helper function to check if a date is a weekend
@RequiresApi(Build.VERSION_CODES.O)
fun isWeekend(date: LocalDate): Boolean {
    return date.dayOfWeek == DayOfWeek.FRIDAY || date.dayOfWeek == DayOfWeek.SATURDAY
}


data class DayInfo(val date: Int, val day: String)

@Composable
fun DateItem(
    day: DayInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF3E7BFA) else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected) Color(0xFF3E7BFA).copy(alpha = 0.1f) else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.date.toString(),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isSelected) Color(0xFF3E7BFA) else Color.Black
            )

            Text(
                text = day.day,
                fontFamily = fontFamily,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

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