package com.example.doctorek.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorekAppBar(
    title: String,
    centerTitle: Boolean = false,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            if (centerTitle) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        navigationIcon = { 
            if (navigationIcon != null) {
                Modifier.padding(start = 16.dp).let { paddingModifier ->
                    navigationIcon()
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,  // Changed from MaterialTheme.colorScheme.background
            titleContentColor = Color.Black  // Changed to match white background
        ),
        windowInsets = WindowInsets(0, 0, 0, 0),
        // Add horizontal padding to the entire app bar
        modifier = modifier.padding(horizontal = 12.dp)
    )
}