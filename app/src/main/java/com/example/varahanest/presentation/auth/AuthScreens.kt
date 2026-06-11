package com.example.varahanest.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.varahanest.R
import com.example.varahanest.domain.model.UserProfile
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (UserProfile) -> Unit,
    onNavigateToOtp: (String) -> Unit // Navigates to OTP and passes the phone number
) {
    val authState by viewModel.authState.collectAsState()
    var phone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    // Ambient floating animation for background gradients
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val bgOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_offset"
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess((authState as AuthState.Success).profile)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC)) // Light Grey Foundation
    ) {
        // Floating ambient blur blobs in background
        Box(
            modifier = Modifier
                .offset(x = bgOffset.dp, y = (-bgOffset).dp)
                .size(400.dp)
                .align(Alignment.TopEnd)
                .background(Color(0x0C00327D), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = (-bgOffset).dp, y = bgOffset.dp)
                .size(300.dp)
                .align(Alignment.BottomStart)
                .background(Color(0x0CFED65B), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo Header
            Image(
                painter = painterResource(id = R.drawable.varaha_logo),
                contentDescription = "Varaha Logo",
                modifier = Modifier
                    .height(96.dp)
                    .width(96.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Login to Varaha Nest",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191B22),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Enter your credentials to access your portfolio",
                fontSize = 14.sp,
                color = Color(0xFF434653),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Login Form Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.5f), Color(0xFFC3C6D5).copy(alpha = 0.3f))
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Mobile Number",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF434653),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            phoneError = null
                        },
                        placeholder = { Text("+91 98765 43210") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = "Phone Icon",
                                tint = Color(0xFF737784)
                            )
                        },
                        isError = phoneError != null,
                        supportingText = phoneError?.let { { Text(it) } } ?: {
                            Text("We'll send a 6-digit OTP to this number", color = Color(0xFF737784), fontSize = 10.sp)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00327D),
                            unfocusedBorderColor = Color(0xFFC3C6D5),
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedContainerColor = Color(0xFFFAF8FF),
                            unfocusedContainerColor = Color(0xFFFAF8FF)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Verify Button
                    Button(
                        onClick = {
                            if (phone.isEmpty()) {
                                phoneError = "Mobile number is required"
                            } else if (phone.length < 10) {
                                phoneError = "Please enter a valid mobile number"
                            } else {
                                viewModel.sendOtp(phone)
                                onNavigateToOtp(phone)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00327D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Brand watermark footer
                    Text(
                        text = "POWERED BY SSP GLOBAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF737784).copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    viewModel: AuthViewModel,
    phone: String, // Dynamic phone passed from Login
    onLoginSuccess: (UserProfile) -> Unit,
    onNavigateBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    var otpText by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var timeLeft by remember { mutableStateOf(59) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = true) {
        // Automatically request focus on OTP field
        focusRequester.requestFocus()
        // Simple resend countdown timer
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess((authState as AuthState.Success).profile)
        } else if (authState is AuthState.Error) {
            otpError = (authState as AuthState.Error).message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF00327D))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F9FC))
            )
        },
        containerColor = Color(0xFFF7F9FC)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFDAE2FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Lock Icon",
                        tint = Color(0xFF00327D),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Verify your identity",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191B22)
                    )
                    Text(
                        text = "Enter the 6-digit code sent to",
                        fontSize = 14.sp,
                        color = Color(0xFF434653),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = phone.ifEmpty { "+91 98765 43210" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00327D),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // 6-digit OTP Custom display (using single hidden text field pattern)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Hidden text field to intercept key inputs
                    androidx.compose.foundation.text.BasicTextField(
                        value = otpText,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                otpText = it
                                otpError = null
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (otpText.length == 6) {
                                    viewModel.verifyOtp(phone, otpText)
                                } else {
                                    otpError = "OTP must be 6 digits"
                                }
                            }
                        ),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .size(1.dp) // Keep it tiny
                    )

                    // 6 separate styling boxes row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clickable { focusRequester.requestFocus() }
                            .padding(vertical = 12.dp)
                    ) {
                        for (i in 0 until 6) {
                            val char = otpText.getOrNull(i)?.toString() ?: ""
                            val isFocused = otpText.length == i
                            Box(
                                modifier = Modifier
                                    .size(width = 46.dp, height = 56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(
                                        width = if (isFocused) 2.dp else 1.dp,
                                        color = if (isFocused) Color(0xFF00327D) else Color(0xFFC3C6D5),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191B22)
                                )
                            }
                        }
                    }
                }

                // Resend timer text
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (timeLeft > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = "Timer", tint = Color(0xFF434653), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Resend code in 0:${if (timeLeft < 10) "0$timeLeft" else timeLeft}",
                                color = Color(0xFF434653),
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Resend code now",
                            color = Color(0xFF00327D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable {
                                    timeLeft = 59
                                    viewModel.sendOtp(phone)
                                }
                                .padding(8.dp)
                        )
                    }
                }

                if (otpError != null) {
                    Text(
                        text = otpError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Verify Button
                Button(
                    onClick = {
                        if (otpText.length == 6) {
                            viewModel.verifyOtp(phone, otpText)
                        } else {
                            otpError = "OTP must be 6 digits"
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00327D)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Verify & Proceed", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                        }
                    }
                }

                // Trust indicators
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "ENTERPRISE TRUST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF737784).copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified User", tint = Color(0xFF737784).copy(alpha = 0.4f), modifier = Modifier.size(24.dp))
                        Icon(Icons.Default.Lock, contentDescription = "Security", tint = Color(0xFF737784).copy(alpha = 0.4f), modifier = Modifier.size(24.dp))
                        Icon(Icons.Default.Warning, contentDescription = "Shield", tint = Color(0xFF737784).copy(alpha = 0.4f), modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
