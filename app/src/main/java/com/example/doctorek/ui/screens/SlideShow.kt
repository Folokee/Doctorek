package com.example.doctorek.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doctorek.AuthScreens
import com.example.doctorek.R
import com.example.doctorek.Screens
import com.example.doctorek.data.auth.SharedPrefs
import kotlinx.coroutines.launch

data class SlideData(
    val imageRes: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlideShow(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefs = SharedPrefs(context)
    sharedPrefs.setFirstTime(false)

    val slides = listOf(
        SlideData(
            imageRes = R.drawable.person1,
            title = "Find Your Doctor",
            description = "Easily search and find the best doctors for your health needs"
        ),
        SlideData(
            imageRes = R.drawable.person2,
            title = "Book Appointments",
            description = "Schedule appointments with just a few taps at your convenience"
        ),
        SlideData(
            imageRes = R.drawable.person3,
            title = "Health Records",
            description = "Keep all your health records in one secure place"
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()

    // List of background colors for each slide
    val slideColors = listOf(
        colorResource(id = R.color.light_blue),
        colorResource(id = R.color.pink),
        colorResource(id = R.color.bottle_green)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Slideshow area (60% horizontally of the screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.4f)
                .background(slideColors[pagerState.currentPage]),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
            ) { page ->
                Image(
                    painter = painterResource(id = slides[page].imageRes),
                    contentDescription = "Slide ${page + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.FillHeight
                )
            }
        }

        // Bottom half (fixed for all slides)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Title and description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = slides[pagerState.currentPage].title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.blue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = slides[pagerState.currentPage].description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Indicator dots
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .background(
                                color = if (isSelected) colorResource(R.color.blue)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    )
                }
            }

            // Buttons
            Column(
                modifier = Modifier.fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                OutlinedButton(
                    onClick = { navController.navigate(AuthScreens.Onboarding.route) },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, color = colorResource(id = R.color.blue)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(id = R.color.blue)
                    ),
                ) {
                    Text(text = "Skip", modifier = Modifier.padding(vertical = 4.dp))
                }


                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate(AuthScreens.Signup.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.blue),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Sign Up", modifier = Modifier.padding(vertical = 4.dp))
                }


            }
        }
    }
}