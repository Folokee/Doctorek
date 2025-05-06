package com.example.doctorek.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doctorek.R
import com.example.doctorek.Screens

@Composable
fun Onboarding(navController: NavController){

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.8f),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "App logo",
                modifier = Modifier.size(200.dp)
            )
        }
        // Buttons
        Column(
            modifier = Modifier.fillMaxWidth(0.8f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Button(
                onClick = { navController.navigate(Screens.Signup.route) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Sign Up", modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { navController.navigate(Screens.Signin.route) },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, color = colorResource(id = R.color.blue)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorResource(id = R.color.blue)
                ),
            ) {
                Text(text = "Sign In", modifier = Modifier.padding(vertical = 4.dp))
            }

        }
    }
}