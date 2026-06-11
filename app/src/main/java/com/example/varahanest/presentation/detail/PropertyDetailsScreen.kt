package com.example.varahanest.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.varahanest.domain.model.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailsScreen(
    propertyId: String,
    viewModel: PropertyDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val property by viewModel.property.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val leadState by viewModel.leadState.collectAsState()
    
    var showLeadDialog by remember { mutableStateOf(false) }
    var leadMessage by remember { mutableStateOf("") }

    LaunchedEffect(propertyId) {
        viewModel.loadPropertyDetails(propertyId)
    }

    Scaffold(
        bottomBar = {
            property?.let { prop ->
                DetailsBottomActions(
                    onWhatsAppClick = {
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=919876543210&text=Hi, I am interested in your listing: ${prop.title} on Varaha Nest.")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    onContactClick = { 
                        leadMessage = "Hi, I am interested in this property. Please contact me with more details."
                        showLeadDialog = true 
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (property == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val prop = property!!
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Header Image with overlay controls
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                prop.imageUrls.firstOrNull()
                                    ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuBJPJiDuQCk8Vxrcn1kkV90_ctV2TdHYW11Q_JFg12XpunJLTlt1YXVK1flSwF-aHtWtScYrR-4z-JH2dBsMtwQp_81UuclYYZANxxAQlqEbwNXUNQSpmPNAtxi4n2w2JswAIizl8ulDiCIQ8oOe-XvFb_lgBKPi1-lT2AQYgn3mAGC7fR2xcGadBpQoRq7YQ3CdOqM96a3vzdMhv5Y4-NH1YIBHUf39a89NwV0ET8DLaQ6FWDJjN1q5KDEc1tZRFYAIMwTsqB0EF1x"
                            ),
                            contentDescription = prop.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient bottom shadow
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                    )
                                )
                        )

                        // Top Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            IconButton(
                                onClick = { viewModel.toggleFavorite() },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color.Red else Color.White
                                )
                            }
                        }
                    }

                    // Content details
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val formattedPrice = when {
                                prop.price >= 10000000 -> "₹${String.format("%.1f", prop.price / 10000000)} Cr"
                                prop.price >= 100000 -> "₹${String.format("%.1f", prop.price / 100000)} L"
                                else -> "₹${prop.price.toInt()}"
                            }
                            Text(
                                text = if (prop.transactionType == "RENT") "$formattedPrice/mo" else formattedPrice,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (prop.verified) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF4CAF50).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        "VERIFIED",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = prop.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${prop.address}, ${prop.city}, ${prop.state}",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Features Spec Sheet Grid
                        Text("Specifications", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecItem(label = "Bedrooms", value = "${prop.bedrooms} BHK", icon = Icons.Default.Home, modifier = Modifier.weight(1f))
                            SpecItem(label = "Bathrooms", value = "${prop.bathrooms} Bath", icon = Icons.Default.Check, modifier = Modifier.weight(1f))
                            SpecItem(label = "Balconies", value = "${prop.balconies} Balc", icon = Icons.Default.Menu, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecItem(label = "Super Area", value = "${prop.areaSqft.toInt()} sqft", icon = Icons.Default.LocationOn, modifier = Modifier.weight(1f))
                            SpecItem(label = "Furnished", value = prop.furnishingStatus?.replace("_", " ") ?: "N/A", icon = Icons.Default.Info, modifier = Modifier.weight(1f))
                            SpecItem(label = "Parking", value = if (prop.parkingSpaces > 0) "${prop.parkingSpaces} Spaces" else "No", icon = Icons.Default.PlayArrow, modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Description
                        Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = prop.description.ifEmpty { "No description provided for this listing." },
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Map / Location detection preview
                        Text("Location Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color.LightGray, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Google Map View Placeholder\n[Lat: ${prop.latitude ?: 28.4}, Lng: ${prop.longitude ?: 77.0}]",
                                textAlign = TextAlign.Center,
                                color = Color.DarkGray,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // Contact Owner Lead Dialog
        if (showLeadDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showLeadDialog = false 
                    viewModel.resetLeadState()
                },
                title = { Text("Contact Property Owner", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Send a message directly to the owner to register your interest:")
                        OutlinedTextField(
                            value = leadMessage,
                            onValueChange = { leadMessage = it },
                            label = { Text("Message") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            maxLines = 4
                        )
                        if (leadState is LeadState.Error) {
                            Text((leadState as LeadState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.submitLead(leadMessage) },
                        enabled = leadState !is LeadState.Loading
                    ) {
                        if (leadState is LeadState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        } else {
                            Text("Send Inquiry")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLeadDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Success Toast/Dialog representation
        if (leadState is LeadState.Success) {
            LaunchedEffect(Unit) {
                showLeadDialog = false
                viewModel.resetLeadState()
            }
        }
    }
}

@Composable
fun SpecItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, fontSize = 10.sp, color = Color.Gray)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun DetailsBottomActions(
    onWhatsAppClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onWhatsAppClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366))
            ) {
                Icon(Icons.Default.Phone, contentDescription = "WhatsApp", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("WhatsApp", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onContactClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text("Contact Owner", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
