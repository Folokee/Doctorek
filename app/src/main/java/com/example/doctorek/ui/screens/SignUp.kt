package com.example.doctorek.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doctorek.AuthActivity
import com.example.doctorek.AuthScreens
import com.example.doctorek.DScreens
import com.example.doctorek.DoctorActivity
import com.example.doctorek.MainActivity
import com.example.doctorek.R
import com.example.doctorek.Screens
import com.example.doctorek.data.auth.SharedPrefs
import com.example.doctorek.data.repositories.Role
import com.example.doctorek.ui.components.LoadingSplash
import com.example.doctorek.ui.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    onFacebookClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    viewModel : AuthViewModel = viewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isDoctorRole by remember { mutableStateOf(false) }
    val userState = viewModel.userState.value
    val context = LocalContext.current
    val sharedPrefs = SharedPrefs(context)

    LaunchedEffect(userState.isSignedIn) {
        if (userState.isSignedIn){
            if (sharedPrefs.getType() != null && sharedPrefs.getType()?.compareTo(Role.Doctor.role) == 0){
                navController.navigate(AuthScreens.DoctorDetails.route)
            } else if (sharedPrefs.getType() != null && sharedPrefs.getType()?.compareTo(
                    Role.Patient.role) == 0){
                navController.navigate(AuthScreens.ProfileDetails.route)
            }
            Toast.makeText(context,"Account created successfully, you can now login", Toast.LENGTH_SHORT).show()
        }
    }

    if (userState.Loading) {
        LoadingSplash()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                alignment = Alignment.Center,
                contentScale = ContentScale.FillHeight
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sign Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        if(userState.errorMessage != null){
            Text(
                text = userState.errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Role Selection Switch
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.light_blue).copy(alpha = 0.2f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (!isDoctorRole)
                                    colorResource(id = R.color.blue)
                                else
                                    Color.Transparent,
                                RoundedCornerShape(28.dp)
                            )
                            .clickable { isDoctorRole = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Patient",
                            fontWeight = FontWeight.Medium,
                            color = if (!isDoctorRole) Color.White else Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (isDoctorRole)
                                    colorResource(id = R.color.blue)
                                else
                                    Color.Transparent,
                                RoundedCornerShape(28.dp)
                            )
                            .clickable { isDoctorRole = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Doctor",
                            fontWeight = FontWeight.Medium,
                            color = if (isDoctorRole) Color.White else Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(text = "example@domain.com", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorResource(id = R.color.light_blue),
                    focusedBorderColor = colorResource(id = R.color.blue),
                    cursorColor = colorResource(id = R.color.blue)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Password",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = { Text(text = "••••••••••••••", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colorResource(R.color.light_blue),
                    focusedBorderColor = colorResource(id = R.color.blue),
                    cursorColor = colorResource(id = R.color.blue)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = {
                val role = if (isDoctorRole) "doctor" else "patient"
                viewModel.signUp(email, password, role)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.blue)
            )
        ) {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
            }
        ) {
            Text(
                text = "Forget The Password ?",
                color = colorResource(id = R.color.blue),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "or continue with",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onFacebookClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.facebook_logo),
                    contentDescription = "Facebook Icon",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Facebook",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }

            OutlinedButton(
                onClick = onGoogleClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Google",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account ? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            TextButton(
                onClick = {
                    navController.navigate(AuthScreens.Signin.route)
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Sign In",
                    color = colorResource(id = R.color.blue),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}