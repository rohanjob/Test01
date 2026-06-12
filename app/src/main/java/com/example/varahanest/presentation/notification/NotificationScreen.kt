package com.example.varahanest.presentation.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

data class NotificationModel(
    val id: String,
    val type: String, // NEW_LISTING, PRICE_DROP, INQUIRY_UPDATE, SYSTEM
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val timeAgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit
) {
    // Terra Color Palette
    val terraPrimary = Color(0xFF4A7C59)
    val terraSurface = Color(0xFFFAF6F0)
    val terraContainer = Color(0xFFF0ECE4)
    val terraContainerLow = Color(0xFFF5F1EA)
    val terraText = Color(0xFF2E3230)
    val terraTextVariant = Color(0xFF4A4E4A)
    val terraOutline = Color(0xFFC4C8BC)
    val terraError = Color(0xFFB83230)

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    var readNotificationIds by remember { mutableStateOf(setOf("inquiry-1", "system-1")) }
    var showMenu by remember { mutableStateOf(false) }

    var notificationsList by remember {
        mutableStateOf(
            listOf(
                NotificationModel(
                    id = "listing-1",
                    type = "NEW_LISTING",
                    title = "NEW LISTING",
                    body = "New 3 BHK Villa available in Sector 88A, Gurugram. Check it out!",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBDVzQMuGazPs5Lic1UWgvYaadEclFzGtxfaFx_1rT9pMpC8TsMJYQqMl9z3FKq42HvVNUW4V_dgjuY2d0N6HYINsh5C6JZnx-x96K4rgsZbjB-KNTGAKyXQL7RgfbzMf1UoV1Ue4Q3bVwS23arB8oHSho1x0ryYXBrwdwNqVexKbuIuLV1WA-xFIExo3wky7wirwivxmp96lr4nU4U4rlVGJFsdAqp_lVwZeipKrIPgACr1B3aXrjU7K_mmEnm4I_7zw_vTgI0K6mj",
                    timeAgo = "2h ago"
                ),
                NotificationModel(
                    id = "price-1",
                    type = "PRICE_DROP",
                    title = "PRICE DROP",
                    body = "Price reduced for Emerald Estates! Now starting at ₹2.0 Cr.",
                    timeAgo = "5h ago"
                ),
                NotificationModel(
                    id = "inquiry-1",
                    type = "INQUIRY_UPDATE",
                    title = "INQUIRY UPDATE",
                    body = "Owner of The Marigold Villas has responded to your inquiry regarding maintenance fees.",
                    timeAgo = "Yesterday"
                ),
                NotificationModel(
                    id = "system-1",
                    type = "SYSTEM",
                    title = "SYSTEM",
                    body = "Your profile was successfully updated. Verified badge is now active.",
                    timeAgo = "3 days ago"
                )
            )
        )
    }

    // Filter logic
    val filteredNotifications = notificationsList.filter { notification ->
        val matchesSearch = notification.body.contains(searchQuery, ignoreCase = true) || 
                            notification.title.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Property" -> notification.type == "NEW_LISTING" || notification.type == "PRICE_DROP"
            "Inquiries" -> notification.type == "INQUIRY_UPDATE"
            "Account" -> notification.type == "SYSTEM"
            else -> true
        }
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Notifications", 
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
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert, 
                                contentDescription = "More options",
                                tint = terraPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(terraSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mark all as read", color = terraText) },
                                onClick = {
                                    readNotificationIds = readNotificationIds + notificationsList.map { it.id }.toSet()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear all", color = terraError) },
                                onClick = {
                                    notificationsList = emptyList()
                                    showMenu = false
                                }
                            )
                        }
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
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search notifications...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = terraPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = terraContainerLow,
                    unfocusedContainerColor = terraContainerLow
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Property", "Inquiries", "Account").forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (isSelected) terraPrimary else terraContainerLow)
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else terraTextVariant
                        )
                    }
                }
            }

            // Notifications List / Empty State
            if (filteredNotifications.isEmpty()) {
                // Empty state layout matching HTML
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(terraContainerLow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications Off",
                            tint = terraOutline,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "No notifications yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = terraText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We'll let you know when something important happens in your neighborhood.",
                        fontSize = 14.sp,
                        color = terraTextVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredNotifications.forEach { item ->
                        val isRead = readNotificationIds.contains(item.id)
                        val opacity = if (isRead) 0.75f else 1.0f

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isRead) terraContainerLow.copy(alpha = 0.5f) else terraContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    readNotificationIds = readNotificationIds + item.id
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // Red unread dot top-right
                                if (!isRead) {
                                    Box(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .size(10.dp)
                                            .background(terraPrimary, CircleShape)
                                            .align(Alignment.TopEnd)
                                    )
                                }

                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Left Image or Icon
                                    if (item.imageUrl != null) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = item.imageUrl),
                                            contentDescription = "Notification Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    color = when (item.type) {
                                                        "PRICE_DROP" -> Color(0xFFF8E0A8).copy(alpha = 0.3f)
                                                        "INQUIRY_UPDATE" -> terraPrimary.copy(alpha = 0.1f)
                                                        else -> terraOutline.copy(alpha = 0.2f)
                                                    },
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (item.type) {
                                                    "PRICE_DROP" -> Icons.Default.KeyboardArrowDown
                                                    "INQUIRY_UPDATE" -> Icons.Default.Email
                                                    else -> Icons.Default.Person
                                                },
                                                contentDescription = null,
                                                tint = when (item.type) {
                                                    "PRICE_DROP" -> Color(0xFF705C30)
                                                    "INQUIRY_UPDATE" -> terraPrimary
                                                    else -> terraTextVariant
                                                }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Details Column
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        ) {
                                            if (item.imageUrl != null) {
                                                Icon(
                                                    imageVector = Icons.Default.Home,
                                                    contentDescription = null,
                                                    tint = terraPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            Text(
                                                text = item.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (item.type) {
                                                    "PRICE_DROP" -> Color(0xFF705C30)
                                                    else -> terraPrimary
                                                },
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        Text(
                                            text = item.body,
                                            fontSize = 13.sp,
                                            fontWeight = if (isRead) FontWeight.Normal else FontWeight.SemiBold,
                                            color = terraText,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = item.timeAgo,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
