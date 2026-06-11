package com.example.varahanest.presentation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.varahanest.theme.PrimaryTerra

data class CategoryMenuItem(
    val label: String,
    val icon: ImageVector,
    val searchType: String // "BUY", "RENT", "COMMERCIAL"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesMenuScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPostProperty: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onCategoryClick: (String) -> Unit // Navigates to search with type
) {
    var selectedTab by remember { mutableStateOf("Buy Residential") }

    val tabs = listOf(
        "Buy Residential" to Icons.Default.Home,
        "Rent / PG" to Icons.Default.Lock,
        "Buy Commercial" to Icons.Default.Domain,
        "Lease Commercial" to Icons.Default.Edit,
        "Sell/Rent" to Icons.Default.Add,
        "Support" to Icons.Default.Info
    )

    val buyResidentialCategories = listOf(
        CategoryMenuItem("Apartment / Flat", Icons.Default.Home, "BUY"),
        CategoryMenuItem("Independent House", Icons.Default.Home, "BUY"),
        CategoryMenuItem("Plot / Land", Icons.Default.Star, "BUY"),
        CategoryMenuItem("Builder Floor", Icons.Default.Home, "BUY"),
        CategoryMenuItem("Duplex", Icons.Default.Home, "BUY"),
        CategoryMenuItem("Penthouse", Icons.Default.Star, "BUY"),
        CategoryMenuItem("Farm House", Icons.Default.Home, "BUY"),
        CategoryMenuItem("Gated Community", Icons.Default.Star, "BUY")
    )

    val rentPgCategories = listOf(
        CategoryMenuItem("Rent Apartment", Icons.Default.Lock, "RENT"),
        CategoryMenuItem("Rent House", Icons.Default.Lock, "RENT"),
        CategoryMenuItem("PG / Hostel", Icons.Default.Person, "RENT"),
        CategoryMenuItem("Shared Room", Icons.Default.Person, "RENT"),
        CategoryMenuItem("Independent Room", Icons.Default.Lock, "RENT"),
        CategoryMenuItem("Studio Apartment", Icons.Default.Lock, "RENT")
    )

    val buyCommercialCategories = listOf(
        CategoryMenuItem("Commercial Office", Icons.Default.Domain, "COMMERCIAL"),
        CategoryMenuItem("Commercial Shop", Icons.Default.ShoppingCart, "COMMERCIAL"),
        CategoryMenuItem("Showroom", Icons.Default.ShoppingCart, "COMMERCIAL"),
        CategoryMenuItem("Land / Plot", Icons.Default.Star, "COMMERCIAL"),
        CategoryMenuItem("Warehouse", Icons.Default.Build, "COMMERCIAL"),
        CategoryMenuItem("Building", Icons.Default.Domain, "COMMERCIAL")
    )

    val leaseCommercialCategories = listOf(
        CategoryMenuItem("Ready to move Offices", Icons.Default.Home, "COMMERCIAL"),
        CategoryMenuItem("Bare shell Offices", Icons.Default.Menu, "COMMERCIAL"),
        CategoryMenuItem("Co-working Offices", Icons.Default.Share, "COMMERCIAL"),
        CategoryMenuItem("Retail Shops / Showrooms", Icons.Default.ShoppingCart, "COMMERCIAL"),
        CategoryMenuItem("Warehouse", Icons.Default.Build, "COMMERCIAL"),
        CategoryMenuItem("Factory / Manufacturing", Icons.Default.Build, "COMMERCIAL"),
        CategoryMenuItem("Plot / Land", Icons.Default.Star, "COMMERCIAL"),
        CategoryMenuItem("Others", Icons.Default.Info, "COMMERCIAL")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Categories", fontWeight = FontWeight.Bold, color = PrimaryTerra) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryTerra)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Left Sidebar drawer
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                tabs.forEach { (tabName, icon) ->
                    val isSelected = selectedTab == tabName
                    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent
                    val contentColor = if (isSelected) PrimaryTerra else Color.Gray

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .clickable {
                                when (tabName) {
                                    "Sell/Rent" -> onNavigateToPostProperty()
                                    "Support" -> onNavigateToSupport()
                                    else -> selectedTab = tabName
                                }
                            }
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tabName,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tabName,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            lineHeight = 12.sp
                        )
                    }
                }
            }

            // Separator
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            )

            // Right Category Grid area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Property Options",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val currentCategories = when (selectedTab) {
                    "Buy Residential" -> buyResidentialCategories
                    "Rent / PG" -> rentPgCategories
                    "Buy Commercial" -> buyCommercialCategories
                    "Lease Commercial" -> leaseCommercialCategories
                    else -> buyResidentialCategories
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentCategories) { item ->
                        Card(
                            onClick = { onCategoryClick(item.searchType) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(PrimaryTerra.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = PrimaryTerra,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = item.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
