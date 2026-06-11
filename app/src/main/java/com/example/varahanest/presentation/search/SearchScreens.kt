package com.example.varahanest.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.varahanest.domain.model.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()
    val transactionType by viewModel.transactionType.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                // Top Search Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    OutlinedTextField(
                        value = query,
                        onValueChange = { 
                            viewModel.onQueryChanged(it)
                            showHistory = it.isEmpty()
                        },
                        placeholder = { Text("Search city, address or project...") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            viewModel.addHistoryQuery(query)
                            viewModel.performSearch()
                            showHistory = false
                        }),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Filters",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Transaction Type Tabs (Buy, Rent, Commercial)
                TabRow(
                    selectedTabIndex = when (transactionType) {
                        "BUY" -> 0
                        "RENT" -> 1
                        else -> 2
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = transactionType == "BUY",
                        onClick = { viewModel.onTransactionTypeChanged("BUY") },
                        text = { Text("Buy", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = transactionType == "RENT",
                        onClick = { viewModel.onTransactionTypeChanged("RENT") },
                        text = { Text("Rent", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = transactionType == "COMMERCIAL",
                        onClick = { viewModel.onTransactionTypeChanged("COMMERCIAL") },
                        text = { Text("Commercial", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (showHistory && query.isEmpty()) {
                // Search History View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = "Recent Searches",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    items(searchHistory) { historyQuery ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onQueryChanged(historyQuery)
                                    viewModel.performSearch()
                                    showHistory = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "History Icon",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = historyQuery, fontSize = 14.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    }
                }
            } else {
                // Search Results Feed
                when (uiState) {
                    is SearchUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is SearchUiState.Success -> {
                        val results = (uiState as SearchUiState.Success).results
                        if (results.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No properties match your search criteria.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(results) { property ->
                                    SearchResultCard(
                                        property = property,
                                        onClick = { onNavigateToDetail(property.id) }
                                    )
                                }
                            }
                        }
                    }
                    is SearchUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = (uiState as SearchUiState.Error).message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    is SearchUiState.Idle -> {
                        // Render empty or initial state
                    }
                }
            }
        }

        // Filter Dialog
        if (showFilterDialog) {
            FilterDialog(
                viewModel = viewModel,
                onDismiss = { showFilterDialog = false },
                onApply = {
                    viewModel.performSearch()
                    showFilterDialog = false
                }
            )
        }
    }
}

@Composable
fun SearchResultCard(
    property: Property,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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
                            .padding(12.dp)
                            .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            "VERIFIED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val formattedPrice = when {
                        property.price >= 10000000 -> "₹${String.format("%.1f", property.price / 10000000)} Cr"
                        property.price >= 100000 -> "₹${String.format("%.1f", property.price / 100000)} L"
                        else -> "₹${property.price.toInt()}"
                    }
                    Text(
                        text = if (property.transactionType == "RENT") "$formattedPrice/mo" else formattedPrice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = property.propertyCategory.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = property.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${property.bedrooms} BHK | ${property.bathrooms} Bath | ${property.areaSqft.toInt()} Sqft",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${property.address}, ${property.city}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    viewModel: SearchViewModel,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    val minPrice by viewModel.minPrice.collectAsState()
    val maxPrice by viewModel.maxPrice.collectAsState()
    val bedrooms by viewModel.bedrooms.collectAsState()
    val furnishedStatus by viewModel.furnishedStatus.collectAsState()
    val postedBy by viewModel.postedBy.collectAsState()
    val verifiedOnly by viewModel.verifiedOnly.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filters", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Price Range Inputs
                Column {
                    Text("Budget Range (₹)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = minPrice?.toString() ?: "",
                            onValueChange = { viewModel.minPrice.value = it.toDoubleOrNull() },
                            label = { Text("Min Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = maxPrice?.toString() ?: "",
                            onValueChange = { viewModel.maxPrice.value = it.toDoubleOrNull() },
                            label = { Text("Max Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // BHK config
                Column {
                    Text("BHK Configuration", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3, 4).forEach { num ->
                            FilterChip(
                                selected = bedrooms == num,
                                onClick = { 
                                    viewModel.bedrooms.value = if (bedrooms == num) null else num 
                                },
                                label = { Text("$num BHK") }
                            )
                        }
                    }
                }

                // Furnishing status
                Column {
                    Text("Furnishing Status", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("FULLY_FURNISHED" to "Furnished", "SEMI_FURNISHED" to "Semi-Furnished", "UNFURNISHED" to "Unfurnished").forEach { (value, label) ->
                            FilterChip(
                                selected = furnishedStatus == value,
                                onClick = { 
                                    viewModel.furnishedStatus.value = if (furnishedStatus == value) null else value 
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                // Verified Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verified Listings Only", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Switch(
                        checked = verifiedOnly,
                        onCheckedChange = { viewModel.verifiedOnly.value = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onApply) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { 
                    viewModel.clearFilters()
                    onDismiss()
                }
            ) {
                Text("Reset All")
            }
        }
    )
}
