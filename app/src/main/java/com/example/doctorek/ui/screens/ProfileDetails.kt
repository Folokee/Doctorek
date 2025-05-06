package com.example.doctorek.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorek.R



@Composable
fun ProfileDetailsScreen(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(id = R.color.blue)
                )
            }

            Text(
                text = "Profile Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Picture Placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, Color.LightGray, CircleShape)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                tint = Color.Gray,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Form Fields
        ProfileField(
            label = "Full Name",
            placeholder = "Full Name",
            value = fullName,
            onValueChange = { fullName = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileField(
            label = "Email",
            placeholder = "Email",
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(16.dp))



        // Gender Dropdown Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Gender",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                placeholder = { Text("Gender") }, // This seems to be a typo in the UI mockup
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color.Gray.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.Gray
                    )
                },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = colorResource(id = R.color.blue),
                    cursorColor = colorResource(id = R.color.blue),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileField(
            label = "Date Of Birth",
            placeholder = "DD - MM - YY",
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileField(
            label = "Address",
            placeholder = "Email", // This seems to be a typo in the UI mockup
            value = address,
            onValueChange = { address = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Next Button
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.blue)
            )
        ) {
            Text(
                text = "Next",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color.Gray.copy(alpha = 0.8f)
                ),
            shape = RoundedCornerShape(28.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = colorResource(id = R.color.blue),
                cursorColor = colorResource(id = R.color.blue),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}