package com.example.varahanest.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.varahanest.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.92f) }
    val opacity = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Run scale and fade in parallel
        delay(100)
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 1000)
        )
    }
    
    LaunchedEffect(key1 = true) {
        opacity.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 1200)
        )
        // Simulated connection setup delay
        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF001946)), // Deep Navy background matching HTML
        contentAlignment = Alignment.Center
    ) {
        // Atmosphere glows (subtle top-left and bottom-right gradients)
        // Main Logo & Brand Text Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Varaha Logo Image
            Image(
                painter = painterResource(id = R.drawable.varaha_logo),
                contentDescription = "Varaha Logo",
                modifier = Modifier
                    .size(180.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Brand Title
            Text(
                text = "Varaha Nest",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFE088), // Secondary fixed color
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "PREMIUM REAL ESTATE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFE088).copy(alpha = 0.6f),
                letterSpacing = 3.sp
            )
        }

        // Loader Ring at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFFE088),
                strokeWidth = 2.dp,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Establishing Secure Connection...",
                fontSize = 12.sp,
                color = Color(0xFFA5BDFF).copy(alpha = 0.4f), // On primary container variant
                letterSpacing = 0.5.sp
            )
        }
    }
}
