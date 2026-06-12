package com.example.varahanest.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.varahanest.domain.model.UserProfile
import com.example.varahanest.presentation.auth.AuthViewModel
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToSupport: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    onUpgradeClick: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("varaha_nest_prefs", Context.MODE_PRIVATE) }
    
    var notificationEnabled by remember { mutableStateOf(prefs.getBoolean("push_notifications", true)) }
    var offlineCacheEnabled by remember { mutableStateOf(prefs.getBoolean("offline_cache", true)) }

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            onNavigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            currentUser?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Profile info card (refined)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Pic",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(user.phoneNumber.ifEmpty { "No phone number" }, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(user.role, color = MaterialTheme.colorScheme.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Premium Membership Card
                    val isAgent = user.role == "AGENT"
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (user.isPremium || isAgent) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) 
                                             else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, 
                            if (user.isPremium || isAgent) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) 
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (user.isPremium || isAgent) Icons.Default.Star else Icons.Default.Lock,
                                        contentDescription = "Subscription Info",
                                        tint = if (user.isPremium || isAgent) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isAgent) "Varaha Agent Account 👑" 
                                                   else if (user.isPremium) "Varaha Premium Member 👑" 
                                                   else "Normal Membership",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = if (isAgent) "All premium properties unlocked with Agent privileges"
                                                   else if (user.isPremium) "Unlimited premium property details unlocked" 
                                                   else "Unlock premium property details",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                            
                            if (!isAgent) {
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                if (user.isPremium) {
                                    OutlinedButton(
                                        onClick = { authViewModel.downgradeFromPremium() },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                                    ) {
                                        Text("Reset Subscription (Developer Test)", fontSize = 12.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = onUpgradeClick,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Get Lifetime Premium - ₹999", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Preferences
                    Text("App Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Notifications
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Push Notifications", fontSize = 14.sp)
                                }
                                Switch(
                                    checked = notificationEnabled,
                                    onCheckedChange = { 
                                        notificationEnabled = it
                                        prefs.edit().putBoolean("push_notifications", it).apply()
                                    }
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))

                            // Offline Sync
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Offline Cache", tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Offline Caching & Sync", fontSize = 14.sp)
                                }
                                Switch(
                                    checked = offlineCacheEnabled,
                                    onCheckedChange = { 
                                        offlineCacheEnabled = it
                                        prefs.edit().putBoolean("offline_cache", it).apply()
                                    }
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))

                            // Support Ticket Center Link
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToSupport() }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = "Support Center", tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Support & Help Desk", fontSize = 14.sp)
                                }
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Go", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))

                            // Settings Screen Link
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToSettings() }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Account Settings", fontSize = 14.sp)
                                }
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Go", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Logout Action button
                    Button(
                        onClick = { authViewModel.logout() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
