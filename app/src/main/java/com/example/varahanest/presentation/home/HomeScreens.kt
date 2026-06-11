package com.example.varahanest.presentation.home

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.varahanest.domain.model.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToPostProperty: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMenu: () -> Unit,
    currentRoute: String
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = com.example.varahanest.R.drawable.varaha_logo),
                            contentDescription = "Varaha Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Varaha Nest",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToNotifications) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(8.dp)
                                    .background(Color.Red, CircleShape)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            VarahaBottomNavigation(
                currentRoute = currentRoute,
                onHomeClick = {},
                onSearchClick = { onNavigateToSearch("") },
                onPostClick = onNavigateToPostProperty,
                onActivityClick = onNavigateToProfile, // Links to activity/profile
                onMenuClick = onNavigateToMenu
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Hero Banner & Search
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // High-impact Banner Image
                Image(
                    painter = rememberAsyncImagePainter("https://lh3.googleusercontent.com/aida-public/AB6AXuBJPJiDuQCk8Vxrcn1kkV90_ctV2TdHYW11Q_JFg12XpunJLTlt1YXVK1flSwF-aHtWtScYrR-4z-JH2dBsMtwQp_81UuclYYZANxxAQlqEbwNXUNQSpmPNAtxi4n2w2JswAIizl8ulDiCIQ8oOe-XvFb_lgBKPi1-lT2AQYgn3mAGC7fR2xcGadBpQoRq7YQ3CdOqM96a3vzdMhv5Y4-NH1YIBHUf39a89NwV0ET8DLaQ6FWDJjN1q5KDEc1tZRFYAIMwTsqB0EF1x"),
                    contentDescription = "Hero Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )
                
                // Overlay text
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 20.dp, bottom = 60.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MAGNIFICENCE RISING",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Text(
                        text = "The Imperial Horizon",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Starting ₹3.5 Cr* | Sector 88A, Gurugram",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }

                // Floating Search Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToSearch("") }
                        .padding(16.dp)
                        .height(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search '3 BHK in Gurugram'",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categories Section
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Explore Opportunities",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Find the perfect property across all categories",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryCard(
                        title = "Buy",
                        icon = Icons.Default.Home,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSearch("BUY") }
                    )
                    CategoryCard(
                        title = "Rent",
                        icon = Icons.Default.Check,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSearch("RENT") }
                    )
                    CategoryCard(
                        title = "Commercial",
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSearch("COMMERCIAL") }
                    )
                    CategoryCard(
                        title = "Add Property",
                        icon = Icons.Default.Add,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPostProperty
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Trending Properties Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Trending Projects",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
                )

                when (state) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is HomeUiState.Success -> {
                        val listings = (state as HomeUiState.Success).listings
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            listings.forEach { property ->
                                ProjectCard(
                                    property = property,
                                    onClick = { onNavigateToDetail(property.id) }
                                )
                            }
                        }
                    }
                    is HomeUiState.Error -> {
                        Text(
                            text = (state as HomeUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Testimonials Section
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "What Our Users Say",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TestimonialCard(
                        initials = "JS",
                        name = "John Smith",
                        role = "Property Owner",
                        comment = "The process of listing my property was incredibly smooth. I found a verified tenant within just 4 days! Highly recommended."
                    )
                    TestimonialCard(
                        initials = "AR",
                        name = "Anita Rao",
                        role = "Buyer",
                        comment = "Varaha Nest provides the best premium experience for property seekers. The search filters are very intuitive and helpful."
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )
        ),
        modifier = modifier.aspectRatio(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ProjectCard(
    property: Property,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .width(240.dp)
            .height(230.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        property.imageUrls.firstOrNull()
                            ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuBJPJiDuQCk8Vxrcn1kkV90_ctV2TdHYW11Q_JFg12XpunJLTlt1YXVK1flSwF-aHtWtScYrR-4z-JH2dBsMtwQp_81UuclYYZANxxAQlqEbwNXUNQSpmPNAtxi4n2w2JswAIizl8ulDiCIQ8oOe-XvFb_lgBKPi1-lT2AQYgn3mAGC7fR2xcGadBpQoRq7YQ3CdOqM96a3vzdMhv5Y4-NH1YIBHUf39a89NwV0ET8DLaQ6FWDJjN1q5KDEc1tZRFYAIMwTsqB0EF1x"
                    ),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (property.verified) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            "PREMIUM",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                val formattedPrice = when {
                    property.price >= 10000000 -> "₹${String.format("%.1f", property.price / 10000000)} Cr"
                    property.price >= 100000 -> "₹${String.format("%.1f", property.price / 100000)} L"
                    else -> "₹${property.price.toInt()}"
                }
                Text(
                    text = if (property.transactionType == "RENT") "$formattedPrice/mo" else formattedPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = property.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${property.address}, ${property.city}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TestimonialCard(
    initials: String,
    name: String,
    role: String,
    comment: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(role, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"$comment\"",
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun VarahaBottomNavigation(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onPostClick: () -> Unit,
    onActivityClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp)
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "search",
            onClick = onSearchClick,
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "post",
            onClick = onPostClick,
            icon = { 
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Sell/Rent", tint = MaterialTheme.colorScheme.primary)
                }
            },
            label = { Text("Sell/Rent", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "activity",
            onClick = onActivityClick,
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Activity") },
            label = { Text("Activity", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "menu",
            onClick = onMenuClick,
            icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
            label = { Text("Menu", fontSize = 10.sp) }
        )
    }
}
