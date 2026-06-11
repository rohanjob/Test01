package com.example.varahanest.presentation.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.varahanest.domain.model.Property

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostPropertyScreen(
    viewModel: PostPropertyViewModel,
    onNavigateBack: () -> Unit,
    onSubmissionSuccess: () -> Unit
) {
    val step by viewModel.currentStep.collectAsState()
    val draft by viewModel.propertyDraft.collectAsState()
    val localImages by viewModel.localImagePaths.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()
    
    val scrollState = rememberScrollState()

    LaunchedEffect(submissionState) {
        if (submissionState is SubmissionState.Success) {
            onSubmissionSuccess()
            viewModel.resetSubmissionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List Property", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) viewModel.previousStep() else onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveDraft() }) {
                        Text("Save Draft", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stepper Indicator
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StepIndicator(num = 1, active = step >= 1, modifier = Modifier.weight(1f))
                    StepIndicator(num = 2, active = step >= 2, modifier = Modifier.weight(1f))
                    StepIndicator(num = 3, active = step >= 3, modifier = Modifier.weight(1f))
                }

                when (step) {
                    1 -> StepBasicDetails(viewModel, draft)
                    2 -> StepPropertySpecs(viewModel, draft)
                    3 -> StepPhotosSubmit(viewModel, draft, localImages, submissionState)
                }
            }
        }
    }
}

@Composable
fun StepIndicator(num: Int, active: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(6.dp)
            .background(
                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(3.dp)
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepBasicDetails(viewModel: PostPropertyViewModel, draft: Property) {
    var transactionType by remember { mutableStateOf(draft.transactionType) }
    var category by remember { mutableStateOf(draft.propertyCategory.ifEmpty { "RESIDENTIAL_APARTMENT" }) }
    var address by remember { mutableStateOf(draft.address.ifEmpty { "123 SSP Global Street" }) }
    var city by remember { mutableStateOf(draft.city.ifEmpty { "Tirupati" }) }
    var state by remember { mutableStateOf(draft.state.ifEmpty { "Andhra Pradesh" }) }
    var latitude by remember { mutableStateOf(draft.latitude ?: 28.459) }
    var longitude by remember { mutableStateOf(draft.longitude ?: 77.026) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Step 1: Basic Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // Transaction Types Choice
        Text("I want to:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("BUY" to "Sell Property", "RENT" to "Rent Out", "COMMERCIAL" to "Commercial Lease").forEach { (type, label) ->
                val selected = transactionType == type
                Button(
                    onClick = { transactionType = type },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    border = if (!selected) ButtonDefaults.outlinedButtonBorder else null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(label, fontSize = 12.sp, maxLines = 1)
                }
            }
        }

        // Category dropdown placeholder
        Text("Property Type:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().clickable {
                // Alternates between categories for demo simplicity
                category = if (category == "RESIDENTIAL_APARTMENT") "RESIDENTIAL_HOUSE" else "RESIDENTIAL_APARTMENT"
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = category.replace("_", " "), fontWeight = FontWeight.Medium)
                Text("Change", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            }
        }

        // Location Info
        Text("Location & Address:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Street Address") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        // Location Detection Button
        Button(
            onClick = {
                // Simulating GPS detection coordinates
                latitude = 28.459
                longitude = 77.026
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (latitude != null) "Location Detected (Lat: $latitude, Lng: $longitude)" else "Detect GPS Location coordinates",
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Action Button
        Button(
            onClick = {
                if (address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty()) {
                    viewModel.updateBasicDetails(transactionType, category, address, city, state, latitude, longitude)
                    viewModel.nextStep()
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Next: Property Specifications", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StepPropertySpecs(viewModel: PostPropertyViewModel, draft: Property) {
    var bedrooms by remember { mutableStateOf(if (draft.bedrooms > 0) draft.bedrooms else 3) }
    var bathrooms by remember { mutableStateOf(if (draft.bathrooms > 0) draft.bathrooms else 3) }
    var balconies by remember { mutableStateOf(draft.balconies) }
    var area by remember { mutableStateOf(if (draft.areaSqft > 0) draft.areaSqft.toString() else "1850") }
    var furnishing by remember { mutableStateOf(draft.furnishingStatus ?: "FULLY_FURNISHED") }
    var parking by remember { mutableStateOf(draft.parkingSpaces) }
    var ownership by remember { mutableStateOf(draft.ownershipType ?: "FREEHOLD") }
    var price by remember { mutableStateOf(if (draft.price > 0) draft.price.toString() else "8500000") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Step 2: Specifications & Pricing", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // Room Configs
        Text("BHK Configuration", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 2, 3, 4).forEach { num ->
                val selected = bedrooms == num
                FilterChip(
                    selected = selected,
                    onClick = { bedrooms = num },
                    label = { Text("$num BHK") }
                )
            }
        }

        // Bathrooms
        Text("Bathrooms", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 2, 3, 4).forEach { num ->
                val selected = bathrooms == num
                FilterChip(
                    selected = selected,
                    onClick = { bathrooms = num },
                    label = { Text("$num") }
                )
            }
        }

        // Area & Balconies
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text("Super Area (sqft)") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = balconies.toString(),
                onValueChange = { balconies = it.toIntOrNull() ?: 0 },
                label = { Text("Balconies") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        // Furnishing Choices
        Text("Furnishing Status", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("FULLY_FURNISHED" to "Furnished", "SEMI_FURNISHED" to "Semi", "UNFURNISHED" to "Unfurnished").forEach { (value, label) ->
                val selected = furnishing == value
                FilterChip(
                    selected = selected,
                    onClick = { furnishing = value },
                    label = { Text(label) }
                )
            }
        }

        // Pricing Info
        Text("Pricing & Ownership", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text(if (draft.transactionType == "RENT") "Expected Rent per Month (₹)" else "Expected Price (₹)") },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("FREEHOLD", "LEASEHOLD", "CO_OPERATIVE").forEach { type ->
                val selected = ownership == type
                FilterChip(
                    selected = selected,
                    onClick = { ownership = type },
                    label = { Text(type.replace("_", " ")) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.previousStep() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Back")
            }
            Button(
                onClick = {
                    val parsedArea = area.toDoubleOrNull() ?: 0.0
                    val parsedPrice = price.toDoubleOrNull() ?: 0.0
                    if (parsedArea > 0 && parsedPrice > 0) {
                        viewModel.updatePropertyDetails(
                            bedrooms, bathrooms, balconies, parsedArea, furnishing, parking, ownership, parsedPrice
                        )
                        viewModel.nextStep()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Next: Photos")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepPhotosSubmit(
    viewModel: PostPropertyViewModel,
    draft: Property,
    localImages: List<String>,
    submissionState: SubmissionState
) {
    var title by remember { mutableStateOf(draft.title.ifEmpty { "Luxury 3 BHK Modern Apartment" }) }
    var description by remember { mutableStateOf(draft.description.ifEmpty { "Stunning 3 BHK apartment with beautiful interiors, modern amenities, a modular kitchen, spacious balconies, and 24/7 security. Prime location near transportation and shopping centers." }) }
    var videoUrl by remember { mutableStateOf(draft.videoUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.addLocalImage(it.toString())
            }
        }
    )

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                videoUrl = it.toString()
                viewModel.updateAdditionalDetails(title, description, it.toString())
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Step 3: Upload Photos & Videos", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // Text input details
        Text("Property Headline & Details", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = title,
            onValueChange = { 
                title = it
                viewModel.updateAdditionalDetails(it, description, if (videoUrl.isNotEmpty()) videoUrl else null)
            },
            label = { Text("Property Title") },
            placeholder = { Text("e.g. Spacious 3 BHK Apartment in Tirupati") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { 
                description = it
                viewModel.updateAdditionalDetails(title, it, if (videoUrl.isNotEmpty()) videoUrl else null)
            },
            label = { Text("Description") },
            placeholder = { Text("Describe highlights, amenities, location, etc...") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 5
        )

        // Upload images section
        Text("Photos (Up to 3)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(
            text = "Upload high quality images of your bedrooms, bathrooms, hall, kitchen and front facade to attract premium buyers.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        // Photo Grid Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            localImages.forEach { imageUri ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri.startsWith("http") || imageUri.startsWith("content")) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback text
                        Text("Photo Selected", fontSize = 9.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                    }
                    IconButton(
                        onClick = { viewModel.removeLocalImage(imageUri) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Remove", modifier = Modifier.size(12.dp))
                    }
                }
            }
            
            // Add Photo card trigger
            if (localImages.size < 3) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Photo", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Upload Video section
        Text("Video Tour (Optional)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        if (videoUrl.isNotEmpty()) {
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🎥", fontSize = 18.sp)
                        Text(
                            text = if (videoUrl.contains("/")) videoUrl.substringAfterLast("/") else "Video selected",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                    IconButton(
                        onClick = {
                            videoUrl = ""
                            viewModel.updateAdditionalDetails(title, description, null)
                        }
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Remove Video")
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    videoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Video", tint = MaterialTheme.colorScheme.primary)
                    Text("Select Video Tour from Device", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Summary review card
        Spacer(modifier = Modifier.height(8.dp))
        Text("Review Property Details", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Transaction Type:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text(draft.transactionType, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Category:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text(draft.propertyCategory.replace("_", " "), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Address:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text("${draft.address}, ${draft.city}", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Specs:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text("${draft.bedrooms} BHK | ${draft.bathrooms} Bath | ${draft.areaSqft.toInt()} Sqft", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Price:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text("₹${draft.price.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.previousStep() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Back")
            }
            Button(
                onClick = {
                    // Update state one last time before submitting to make sure default text is saved
                    viewModel.updateAdditionalDetails(title, description, if (videoUrl.isNotEmpty()) videoUrl else null)
                    viewModel.submitListing()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(56.dp),
                enabled = submissionState !is SubmissionState.Loading
            ) {
                if (submissionState is SubmissionState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Publish Property")
                }
            }
        }

        if (submissionState is SubmissionState.Error) {
            Text(
                text = submissionState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
