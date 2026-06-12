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
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

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

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE) }

    var preferredCity by remember { mutableStateOf(prefs.getString("pref_city", "New Delhi") ?: "New Delhi") }
    var preferredLanguage by remember { mutableStateOf(prefs.getString("pref_language", "English (US)") ?: "English (US)") }
    var preferredCurrency by remember { mutableStateOf(prefs.getString("pref_currency", "Indian Rupee (₹)") ?: "Indian Rupee (₹)") }
    var isGoogleLinked by remember { mutableStateOf(prefs.getBoolean("is_google_linked", false)) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLinkedAccountsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showDeleteDataDialog by remember { mutableStateOf(false) }

    var showCityDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }

    var pushEnabled by remember { mutableStateOf(prefs.getBoolean("push_notifications", true)) }
    var emailEnabled by remember { mutableStateOf(prefs.getBoolean("email_alerts", true)) }
    var smsEnabled by remember { mutableStateOf(prefs.getBoolean("sms_alerts", false)) }
    var biometricEnabled by remember { mutableStateOf(prefs.getBoolean("biometric_login", true)) }

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
                        onClick = { showEditProfileDialog = true }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = { showChangePasswordDialog = true }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Share,
                        title = "Linked Accounts",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = { showLinkedAccountsDialog = true }
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
                        value = preferredCity,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        valueColor = terraPrimary,
                        onClick = { showCityDialog = true }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsValueRow(
                        icon = Icons.Default.Menu,
                        title = "Language",
                        value = preferredLanguage,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        valueColor = terraTextVariant,
                        onClick = { showLanguageDialog = true }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsValueRow(
                        icon = Icons.Default.ShoppingCart,
                        title = "Currency",
                        value = preferredCurrency,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        valueColor = terraTextVariant,
                        onClick = { showCurrencyDialog = true }
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
                        onCheckedChange = { pushEnabled = it; prefs.edit().putBoolean("push_notifications", it).apply() }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsToggleRow(
                        icon = Icons.Default.Email,
                        title = "Email Alerts",
                        subtitle = "Weekly property digests",
                        checked = emailEnabled,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onCheckedChange = { emailEnabled = it; prefs.edit().putBoolean("email_alerts", it).apply() }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsToggleRow(
                        icon = Icons.Default.Phone,
                        title = "SMS Alerts",
                        subtitle = "Urgent tour updates",
                        checked = smsEnabled,
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onCheckedChange = { smsEnabled = it; prefs.edit().putBoolean("sms_alerts", it).apply() }
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
                        onCheckedChange = { biometricEnabled = it; prefs.edit().putBoolean("biometric_login", it).apply() }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Star,
                        title = "Privacy Policy",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = { showPrivacyDialog = true }
                    )
                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Info,
                        title = "Terms of Service",
                        iconColor = terraPrimary,
                        textColor = terraText,
                        onClick = { showTermsDialog = true }
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
                        onClick = { showDeactivateDialog = true }
                    )
                    HorizontalDivider(color = terraError.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionRow(
                        icon = Icons.Default.Delete,
                        title = "Delete Data",
                        iconColor = terraError,
                        textColor = terraError,
                        onClick = { showDeleteDataDialog = true }
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

    // --- DIALOGS ---

    // 1. Edit Profile Dialog
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(currentUser?.fullName ?: "") }
        var phoneInput by remember { mutableStateOf(currentUser?.phoneNumber ?: "") }
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = terraPrimary,
                            focusedLabelColor = terraPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = terraPrimary,
                            focusedLabelColor = terraPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.trim().isEmpty()) {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                        } else if (phoneInput.trim().isEmpty()) {
                            Toast.makeText(context, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
                        } else {
                            authViewModel.updateUserProfile(nameInput.trim(), phoneInput.trim())
                            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = terraPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = terraTextVariant)
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 2. Change Password Dialog
    if (showChangePasswordDialog) {
        var currentPsw by remember { mutableStateOf("") }
        var newPsw by remember { mutableStateOf("") }
        var confirmNewPsw by remember { mutableStateOf("") }
        var currentVisible by remember { mutableStateOf(false) }
        var newVisible by remember { mutableStateOf(false) }
        var confirmVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("Change Password", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentPsw,
                        onValueChange = { currentPsw = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentVisible = !currentVisible }) {
                                Icon(
                                    imageVector = if (currentVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = terraPrimary,
                            focusedLabelColor = terraPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPsw,
                        onValueChange = { newPsw = it },
                        label = { Text("New Password") },
                        singleLine = true,
                        visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newVisible = !newVisible }) {
                                Icon(
                                    imageVector = if (newVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = terraPrimary,
                            focusedLabelColor = terraPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmNewPsw,
                        onValueChange = { confirmNewPsw = it },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(
                                    imageVector = if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = terraPrimary,
                            focusedLabelColor = terraPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPsw.isEmpty()) {
                            Toast.makeText(context, "Current password is required", Toast.LENGTH_SHORT).show()
                        } else if (newPsw.length < 6) {
                            Toast.makeText(context, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        } else if (newPsw != confirmNewPsw) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                            showChangePasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = terraPrimary)
                ) {
                    Text("Change Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancel", color = terraTextVariant)
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 3. Linked Accounts Dialog
    if (showLinkedAccountsDialog) {
        var isLinkingGoogle by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = { showLinkedAccountsDialog = false },
            title = { Text("Linked Accounts", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Google
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Google Account", fontWeight = FontWeight.Bold, color = terraText)
                            Text(
                                text = if (isGoogleLinked) "Connected as user@gmail.com" else "Not Connected",
                                fontSize = 12.sp,
                                color = terraTextVariant
                            )
                        }
                        if (isLinkingGoogle) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = terraPrimary, strokeWidth = 2.dp)
                        } else {
                            Button(
                                onClick = {
                                    if (isGoogleLinked) {
                                        isGoogleLinked = false
                                        prefs.edit().putBoolean("is_google_linked", false).apply()
                                        Toast.makeText(context, "Google account unlinked", Toast.LENGTH_SHORT).show()
                                    } else {
                                        isLinkingGoogle = true
                                        scope.launch {
                                            kotlinx.coroutines.delay(1500)
                                            isLinkingGoogle = false
                                            isGoogleLinked = true
                                            prefs.edit().putBoolean("is_google_linked", true).apply()
                                            Toast.makeText(context, "Linked Google account successfully", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isGoogleLinked) terraError.copy(alpha = 0.1f) else terraPrimary,
                                    contentColor = if (isGoogleLinked) terraError else Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isGoogleLinked) "Unlink" else "Link")
                            }
                        }
                    }

                    HorizontalDivider(color = terraOutline.copy(alpha = 0.2f))

                    // Phone Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Phone Number", fontWeight = FontWeight.Bold, color = terraText)
                            Text(
                                text = "Connected (${currentUser?.phoneNumber ?: "+919876543210"})",
                                fontSize = 12.sp,
                                color = terraTextVariant
                            )
                        }
                        Text("Default", fontWeight = FontWeight.Bold, color = terraPrimary, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showLinkedAccountsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = terraPrimary)
                ) {
                    Text("Done")
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 4. Preferred City Dialog
    if (showCityDialog) {
        val cities = listOf("New Delhi", "Mumbai", "Gurugram", "Bengaluru", "Pune", "Hyderabad")
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("Select Preferred City", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    cities.forEach { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    preferredCity = city
                                    prefs.edit().putString("pref_city", city).apply()
                                    showCityDialog = false
                                    Toast.makeText(context, "Preferred City updated to $city", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(city, color = terraText, fontWeight = if (preferredCity == city) FontWeight.Bold else FontWeight.Normal)
                            if (preferredCity == city) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = terraPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Close", color = terraTextVariant)
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 5. Language Dialog
    if (showLanguageDialog) {
        val languages = listOf("English (US)", "English (UK)", "Hindi (हिन्दी)", "Spanish (Español)", "French (Français)")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    preferredLanguage = lang
                                    prefs.edit().putString("pref_language", lang).apply()
                                    showLanguageDialog = false
                                    Toast.makeText(context, "Language updated to $lang", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lang, color = terraText, fontWeight = if (preferredLanguage == lang) FontWeight.Bold else FontWeight.Normal)
                            if (preferredLanguage == lang) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = terraPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = terraTextVariant)
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 6. Currency Dialog
    if (showCurrencyDialog) {
        val currencies = listOf("Indian Rupee (₹)", "US Dollar ($)", "Euro (€)", "Pound Sterling (£)")
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Currency", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    currencies.forEach { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    preferredCurrency = currency
                                    prefs.edit().putString("pref_currency", currency).apply()
                                    showCurrencyDialog = false
                                    Toast.makeText(context, "Currency updated to $currency", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(currency, color = terraText, fontWeight = if (preferredCurrency == currency) FontWeight.Bold else FontWeight.Normal)
                            if (preferredCurrency == currency) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = terraPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close", color = terraTextVariant)
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 7. Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Last Updated: June 12, 2026",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = terraTextVariant
                    )
                    Text(
                        "Varaha Nest respects your privacy and is committed to protecting your personal data. This privacy policy informs you about how we handle your personal data when you visit our application and tells you about your privacy rights.",
                        fontSize = 13.sp,
                        color = terraText
                    )
                    Text(
                        "1. Information We Collect",
                        fontWeight = FontWeight.Bold,
                        color = terraPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        "We may collect, use, store and transfer different kinds of personal data about you which we have grouped together: Identity Data (name, username), Contact Data (email, phone number), Location Data (city preferences), and Transaction Data.",
                        fontSize = 13.sp,
                        color = terraText
                    )
                    Text(
                        "2. How We Use Your Data",
                        fontWeight = FontWeight.Bold,
                        color = terraPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        "We will only use your personal data when the law allows us to. Most commonly, we will use your data to register you as a new user, manage listings you post, verify premium membership payments, and send push notification alerts.",
                        fontSize = 13.sp,
                        color = terraText
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = terraPrimary)
                ) {
                    Text("I Understand")
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 8. Terms of Service Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service", fontWeight = FontWeight.Bold, color = terraPrimary) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Last Updated: June 12, 2026",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = terraTextVariant
                    )
                    Text(
                        "By accessing or using the Varaha Nest mobile application, you agree to comply with and be bound by these Terms of Service. Please review them carefully.",
                        fontSize = 13.sp,
                        color = terraText
                    )
                    Text(
                        "1. User Account & Security",
                        fontWeight = FontWeight.Bold,
                        color = terraPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        "You are responsible for safeguarding the credentials you use to access the application. You agree to notify us immediately of any unauthorized use of your account.",
                        fontSize = 13.sp,
                        color = terraText
                    )
                    Text(
                        "2. Property Listings",
                        fontWeight = FontWeight.Bold,
                        color = terraPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        "Users are solely responsible for the accuracy, truthfulness, and legality of any property listing they post. Premium properties require payment upgrades for non-agent users.",
                        fontSize = 13.sp,
                        color = terraText
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = terraPrimary)
                ) {
                    Text("Accept")
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 9. Deactivate Account Confirmation Dialog
    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text("Deactivate Account?", fontWeight = FontWeight.Bold, color = terraError) },
            text = {
                Text(
                    "Are you sure you want to deactivate your account? This will temporarily disable your profile and hide your properties. You can reactivate anytime by logging back in.",
                    color = terraText,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeactivateDialog = false
                        authViewModel.logout()
                        Toast.makeText(context, "Account deactivated successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = terraError)
                ) {
                    Text("Deactivate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text("Cancel", color = terraTextVariant)
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 10. Delete Data Confirmation Dialog
    if (showDeleteDataDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDataDialog = false },
            title = { Text("Permanently Delete All Data?", fontWeight = FontWeight.Bold, color = terraError) },
            text = {
                Text(
                    "This action is permanent and cannot be undone. All your listings, drafted properties, favorite items, preferences, and profile records will be permanently wiped from our database.",
                    color = terraText,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDataDialog = false
                        authViewModel.deleteUserDataAndLogout()
                        Toast.makeText(context, "All user data deleted successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = terraError)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDataDialog = false }) {
                    Text("Cancel", color = terraTextVariant)
                }
            },
            containerColor = terraSurface,
            shape = RoundedCornerShape(16.dp)
        )
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
    valueColor: Color,
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
