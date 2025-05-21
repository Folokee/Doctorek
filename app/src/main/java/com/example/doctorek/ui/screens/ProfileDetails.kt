package com.example.doctorek.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.AuthActivity
import com.example.doctorek.MainActivity
import com.example.doctorek.R
import com.example.doctorek.Screens
import com.example.doctorek.ui.viewmodels.ProfileViewModel


@Composable
fun ProfileDetailsScreen(
    viewModel : ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.profileState.collectAsState()


    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as AuthActivity).finish()
        }
    }

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

            Text(
                text = "Profile Details",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(R.color.blue)
                )
            }
        } else if (uiState.profile.email != "") {
            var fullName by remember { mutableStateOf(uiState.profile.full_name) }
            var email by remember { mutableStateOf(uiState.profile.email) }
            var gender by remember { mutableStateOf("") }
            var dateOfBirth by remember { mutableStateOf("") }
            var address by remember { mutableStateOf(uiState.profile.address) }
            var phone by remember { mutableStateOf(uiState.profile.phone_number) }

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
                value = fullName?: "",
                onValueChange = { fullName = it }
            )
            ProfileField(
                label = "Email",
                placeholder = "Email",
                value = email?: "",
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(16.dp))


            // Gender Dropdown Field
            /*
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

        Spacer(modifier = Modifier.height(16.dp))*/

            ProfileField(
                label = "Address",
                placeholder = "Address",
                value = address?: "",
                onValueChange = { address = it }
            )

            ProfileField(
                label = "Phone Number",
                placeholder = "Phone Number",
                value = phone?: "",
                onValueChange = { phone = it }
            )

            Spacer(modifier = Modifier.weight(1f))


            // Next Button
            Button(
                onClick = {
                    viewModel.updateProfile(
                        email = email!!,
                        phone_number = phone!!,
                        full_name = fullName!!,
                        address = address!!,
                        avatar_url = ""
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue)
                )
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.blue),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Save",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "An error occurred while loading the profile",
                    fontSize = 20.sp,
                )
                Button(
                    onClick = {
                        viewModel.getProfile()
                    }
                ) {
                    Text("Try Again")
                }
            }
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