package com.example.varahanest.presentation.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.varahanest.R

@Composable
fun SspIntroductionScreen(
    authViewModel: AuthViewModel,
    onContinueToPortal: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.hill),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Asymmetric atmosphere blurs (reduced opacity to blend with background image)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE2E2EB).copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Main Content Glass Card
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.6f), Color(0xFFC3C6D5).copy(alpha = 0.2f))
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Branding Section
                    Image(
                        painter = painterResource(id = R.drawable.ssp_logo),
                        contentDescription = "SSP Global Corporate Logo",
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SOFTWARE SOLUTIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00327D).copy(alpha = 0.8f),
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Central Content Section
                    Text(
                        text = "Software Solution",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00327D),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "A Software Organization in Tirupati. All Software Services Are Available.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF191B22),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(color = Color(0xFF00327D).copy(alpha = 0.1f), modifier = Modifier.width(280.dp))

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Real-time portal bridging Varaha Nest land parameters with SSP high-performance cloud databases.",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF434653),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Buttons Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // TRY IT (WhatsApp Contact)
                        Button(
                            onClick = {
                                val url = "https://wa.me/919035011859?text=I%20want%20to%20know%20more%20about%20your%20services."
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00327D)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("TRY IT", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Arrow", modifier = Modifier.size(20.dp))
                            }
                        }

                        // CONTINUE TO PORTAL
                        OutlinedButton(
                            onClick = onContinueToPortal,
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder().copy(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(colors = listOf(Color(0xFF00327D), Color(0xFF00327D)))
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00327D)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("CONTINUE TO PORTAL »", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // User Info Footer
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF00327D).copy(alpha = 0.05f), RoundedCornerShape(99.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User Account",
                            tint = Color(0xFF00327D),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logged in as ",
                            fontSize = 12.sp,
                            color = Color(0xFF191B22)
                        )
                        Text(
                            text = currentUser?.fullName ?: "Google_User",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00327D)
                        )
                        Text(
                            text = " (${currentUser?.role ?: "BUYER"})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00327D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
