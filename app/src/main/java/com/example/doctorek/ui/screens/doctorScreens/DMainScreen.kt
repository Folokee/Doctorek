package com.example.doctorek.ui.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doctorek.DScreens
import com.example.doctorek.R
import com.example.doctorek.Screens
import com.example.doctorek.ui.components.DoctorekAppBar
import com.example.doctorek.ui.screens.BottomNavItem

@Composable
fun DMainScreen(
    navController: NavController,
) {
    val items = listOf(
        BottomNavItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = DScreens.Home.route
        ),
        BottomNavItem(
            title = "Prescriptions",
            icon = Icons.Default.ListAlt,
            route = DScreens.Prescriptions.route
        ),
        BottomNavItem(
            title = "QR Scanner",
            icon = Icons.Default.QrCode,
            route = DScreens.QRCode.route
        ),
        BottomNavItem(
            title= "Profile",
            icon = Icons.Default.Person,
            route = DScreens.Profile.route
        )
    )

    var selectedItem by remember { mutableStateOf(items[0]) }

    val activeItemBackground = colorResource(id = R.color.light_blue).copy(alpha = 0.25f)
    val activeItemForegroundColor = colorResource(id = R.color.nav_bar_active_item)
    val inactiveItemColor = colorResource(id = R.color.nav_bar_active_item)
    val navBarContainerColor = colorResource(id = R.color.white)

    Scaffold (
        bottomBar = {
            NavigationBar(
                containerColor = navBarContainerColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
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
        },
        containerColor = colorResource(R.color.white)
    ){innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .background(colorResource(R.color.white))
        ) {
            when (selectedItem.route) {
                DScreens.Home.route -> {
                    HomeScreen(navController)
                }
                DScreens.Prescriptions.route -> {
                    PrescriptionsScreen(navController)
                }
                DScreens.QRCode.route -> {
                    QRCodeScreen(navController)
                }
                DScreens.Profile.route -> {
                    DoctorProfile(navController)
                }
            }
        }
    }

}
