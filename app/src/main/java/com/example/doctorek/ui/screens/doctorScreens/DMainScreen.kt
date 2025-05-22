package com.example.doctorek.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.Screens

@Composable
fun DMainScreen(
    navController: NavController,
) {
    val items = listOf(
        BottomNavItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = Screens.Home.route
        ),
        BottomNavItem(
            title = "Appointments",
            icon = Icons.Default.DateRange,
            route = Screens.Appointments.route
        ),
        BottomNavItem(
            title = "Prescriptions",
            icon = Icons.Default.MedicalServices,
            route = Screens.Prescriptions.route
        ),
        BottomNavItem(
            title = "Profile",
            icon = Icons.Default.Person,
            route = Screens.Profile.route
        )
    )

    var selectedItem by remember { mutableStateOf(items[0]) }

    val activeItemBackground = colorResource(id = R.color.light_blue).copy(alpha = 0.25f)
    val activeItemForegroundColor = colorResource(id = R.color.nav_bar_active_item)
    val inactiveItemColor = colorResource(id = R.color.nav_bar_active_item)
    val navBarContainerColor = colorResource(id = R.color.white)

}