package com.example.varahanest.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.varahanest.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    // Terra Color Palette
    val terraPrimary = Color(0xFF4A7C59)
    val terraSurface = Color(0xFFFAF6F0)
    val terraContainer = Color(0xFFF0ECE4)
    val terraContainerLow = Color(0xFFF5F1EA)
    val terraText = Color(0xFF2E3230)
    val terraTextVariant = Color(0xFF4A4E4A)
    val terraError = Color(0xFFB83230)
    val terraOutline = Color(0xFFC4C8BC)

    var pushEnabled by remember { mutableStateOf(true) }
    var emailEnabled by remember { mutableStateOf(true) }
    var smsEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            onLoggedOut()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        fontWeight = FontWeight.Bold,
                        color = terraPrimary,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = terraPrimary
                        )
                    }
                },
                actions = {
                    // Avatar/User badge
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .background(terraPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User profile icon",
                            tint = terraPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = terraSurface)
            )
        },
        containerColor = terraSurface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // 1. Account Settings Section
            SettingsSectionHeader(title = "Account Settings", textColor = terraTextVariant)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = terraContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsActionRow(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = {}
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = {}
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Share,
                        title = "Linked Accounts",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = {}
                    )
                }
            }

            // 2. Preferences Section
            SettingsSectionHeader(title = "Preferences", textColor = terraTextVariant)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = terraContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsValueRow(
                        icon = Icons.Default.LocationOn,
                        title = "Preferred City",
                        value = "New Delhi",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        valueColor = terraPrimary
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsValueRow(
                        icon = Icons.Default.Menu,
                        title = "Language",
                        value = "English (US)",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        valueColor = terraTextVariant
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsValueRow(
                        icon = Icons.Default.ShoppingCart,
                        title = "Currency",
                        value = "Indian Rupee (₹)",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        valueColor = terraTextVariant
                    )
                }
            }

            // 3. Notifications Section
            SettingsSectionHeader(title = "Notifications", textColor = terraTextVariant)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = terraContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsToggleRow(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Real-time alerts for new listings",
                        checked = pushEnabled,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onCheckedChange = { pushEnabled = it }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsToggleRow(
                        icon = Icons.Default.Email,
                        title = "Email Alerts",
                        subtitle = "Weekly property digests",
                        checked = emailEnabled,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onCheckedChange = { emailEnabled = it }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsToggleRow(
                        icon = Icons.Default.Phone,
                        title = "SMS Alerts",
                        subtitle = "Urgent tour updates",
                        checked = smsEnabled,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onCheckedChange = { smsEnabled = it }
                    )
                }
            }

            // 4. Security & Privacy
            SettingsSectionHeader(title = "Security & Privacy", textColor = terraTextVariant)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = terraContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsToggleRow(
                        icon = Icons.Default.ThumbUp,
                        title = "Biometric Login",
                        subtitle = "Use fingerprint or face recognition",
                        checked = biometricEnabled,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onCheckedChange = { biometricEnabled = it }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Star,
                        title = "Privacy Policy",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = {}
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Info,
                        title = "Terms of Service",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = {}
                    )
                }
            }

            // 5. Support & About
            SettingsSectionHeader(title = "Support & About", textColor = terraTextVariant)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = terraContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsActionRow(
                        icon = Icons.Default.Build,
                        title = "Help Center",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = onNavigateToSupport
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Call,
                        title = "Contact Us",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = onNavigateToSupport
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = terraPrimary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("About Varaha Nest", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = terraText)
                            Text("v1.0.4 - Building Sustainable Living", fontSize = 12.sp, color = terraTextVariant)
                        }
                    }
                }
            }

            // 6. Danger Zone
            SettingsSectionHeader(title = "Danger Zone", textColor = terraError)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = terraError.copy(alpha = 0.05f)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(terraError.copy(alpha = 0.2f))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsActionRow(
                        icon = Icons.Default.Warning,
                        title = "Deactivate Account",
                        iconColor = terraError,
                        textColor = terraError,
                        onClick = {}
                    )
                    HorizontalDivider(color = terraError.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Delete,
                        title = "Delete Data",
                        iconColor = terraError,
                        textColor = terraError,
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Action Button
            Button(
                onClick = { authViewModel.logout() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = terraError),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log Out")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Proudly rooted in nature & technology.",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = terraTextVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, textColor: Color) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    iconColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, 
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsValueRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    iconColor: Color,
    textColor: Color,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    iconColor: Color,
    textColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = iconColor
            )
        )
    }
}
