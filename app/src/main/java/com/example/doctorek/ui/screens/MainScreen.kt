package com.example.doctorek.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.Screens

@Composable
fun MainScreen(navController: NavController) {
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

    val activeItemBackground = colorResource(id = R.color.light_blue)
    val activeItemForegroundColor = colorResource(id = R.color.nav_bar_active_item)
    val inactiveItemColor = colorResource(id = R.color.nav_bar_inactive_item)
    val navBarContainerColor = Color.White

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = navBarContainerColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .shadow(elevation = 8.dp)
            ) {
                items.forEach { item ->
                    val isSelected = selectedItem == item
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (selectedItem != item) {
                                selectedItem = item
                            }
                        },
                        icon = {
                            if (isSelected) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight(0.8f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(activeItemBackground)
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = activeItemForegroundColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.title,
                                        color = activeItemForegroundColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = inactiveItemColor
                                )
                            }
                        },
                        label = null,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = activeItemForegroundColor,
                            unselectedIconColor = inactiveItemColor
                        ),
                        modifier = Modifier.weight(if (isSelected) 1.5f else 1.0f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedItem.route) {
                Screens.Home.route -> HomeScreen(navController)
                Screens.Appointments.route -> AppointmentsScreen(
                    navController = navController,
                )
                Screens.Prescriptions.route -> PrescriptionsScreen(
                    navController = navController,
                )
                Screens.Profile.route -> ProfileDetailsScreen(
                    navController = navController,
                )
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)