package com.example.doctorek.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.doctorek.R
import com.example.doctorek.ui.screens.appointementScreens.sourceSansPro


@Composable
fun AppointmentFailureModal(
    doctorName: String,
    onDismiss: () -> Unit,  // Important to include this callback
    onTryAgain: () -> Unit  // Important to include this callback
) {
    Dialog(
        onDismissRequest = onDismiss,  // Use the callback here
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon with pink background
                Box(
                    modifier = Modifier
                        .size(150.dp) // total area including shadow
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            ambientColor = Color(0x55EA4335), // soft red glow
                            spotColor = Color(0x55EA4335)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.failuremodal),
                        contentDescription = null,
                        modifier = Modifier.size(300.dp) // adjust as needed
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))

                // "Oops" text
                Text(
                    text = "Oops , Failed",
                    fontFamily = sourceSansPro,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5F7E)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Error message
                Text(
                    text = "You appointment booking successfully completed . $doctorName will Message you soon",
                    fontFamily = sourceSansPro,
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Try again button
                Button(
                    onClick = onTryAgain,  // Use the callback here
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4)
                    )
                ) {
                    Text(
                        text = "Try again",
                        fontFamily = sourceSansPro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}