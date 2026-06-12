package com.example.varahanest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.varahanest.domain.model.Property
import com.example.varahanest.domain.usecase.*
import com.example.varahanest.presentation.auth.LoginScreen
import com.example.varahanest.presentation.auth.AgentLoginScreen
import com.example.varahanest.presentation.auth.OtpVerificationScreen
import com.example.varahanest.presentation.auth.SspIntroductionScreen
import com.example.varahanest.presentation.auth.AuthViewModel
import com.example.varahanest.presentation.home.HomeScreen
import com.example.varahanest.presentation.home.HomeViewModel
import com.example.varahanest.presentation.search.SearchScreen
import com.example.varahanest.presentation.search.SearchViewModel
import com.example.varahanest.presentation.detail.PropertyDetailsScreen
import com.example.varahanest.presentation.detail.PropertyDetailsViewModel
import com.example.varahanest.presentation.post.PostPropertyScreen
import com.example.varahanest.presentation.post.PostPropertyViewModel
import com.example.varahanest.presentation.support.SupportCenterScreen
import com.example.varahanest.presentation.support.SupportCenterViewModel
import com.example.varahanest.presentation.profile.ProfileScreen
import com.example.varahanest.presentation.splash.SplashScreen
import com.example.varahanest.presentation.settings.SettingsScreen
import com.example.varahanest.presentation.notification.NotificationsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as VarahaApplication
    val container = context.container

    // Explicitly configure AuthViewModel using Use Cases from AppContainer
    val authViewModel: AuthViewModel = viewModel {
        AuthViewModel(
            context,
            container.database,
            LoginUseCase(container.authRepository),
            RegisterUseCase(container.authRepository),
            SendOtpUseCase(container.authRepository),
            VerifyOtpUseCase(container.authRepository),
            GetCurrentUserUseCase(container.authRepository),
            LogoutUseCase(container.authRepository)
        )
    }

    val scope = rememberCoroutineScope()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isPremium = (currentUser?.isPremium ?: false) || (currentUser?.role == "AGENT")

    var showPaymentDialogForProperty by remember { mutableStateOf<Property?>(null) }
    var showPaymentGatewayOnly by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Authentication (Login)
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { _ ->
                    navController.navigate("ssp") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToOtp = { phoneNumber ->
                    navController.navigate("otp/$phoneNumber")
                },
                onNavigateToAgentLogin = {
                    navController.navigate("agent_login")
                }
            )
        }

        // Agent Login Screen
        composable("agent_login") {
            AgentLoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { _ ->
                    navController.navigate("ssp") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Phone OTP Verification
        composable(
            route = "otp/{phone}",
            arguments = listOf(
                navArgument("phone") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            OtpVerificationScreen(
                viewModel = authViewModel,
                phone = phone,
                onLoginSuccess = { _ ->
                    navController.navigate("ssp") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // SSP Portal Page after Login
        composable("ssp") {
            SspIntroductionScreen(
                authViewModel = authViewModel,
                onContinueToPortal = {
                    navController.navigate("home") {
                        popUpTo("ssp") { inclusive = true }
                    }
                }
            )
        }

        // Home Feed Dashboard
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel {
                HomeViewModel(
                    GetPropertiesUseCase(container.propertyRepository),
                    ToggleFavoriteUseCase(container.propertyRepository)
                )
            }
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSearch = { category ->
                    val route = if (category.isNotEmpty()) "search?type=$category" else "search"
                    navController.navigate(route)
                },
                onNavigateToDetail = { id ->
                    scope.launch {
                        try {
                            val property = container.propertyRepository.getPropertyDetails(id).first()
                            if (property != null) {
                                val user = authViewModel.currentUser.value
                                val currentIsPremium = (user?.isPremium ?: false) || (user?.role == "AGENT")
                                if (property.verified && !currentIsPremium) {
                                    showPaymentDialogForProperty = property
                                } else {
                                    navController.navigate("detail/$id")
                                }
                            }
                        } catch (e: Exception) {
                            navController.navigate("detail/$id")
                        }
                    }
                },
                onNavigateToPostProperty = {
                    navController.navigate("post")
                },
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                },
                onNavigateToSupport = {
                    navController.navigate("support")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToMenu = {
                    navController.navigate("menu")
                },
                currentRoute = "home"
            )
        }

        // Property Search & Filters
        composable(
            route = "search?type={type}",
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "BUY"
                }
            )
        ) { backStackEntry ->
            val searchViewModel: SearchViewModel = viewModel {
                SearchViewModel(
                    GetPropertiesUseCase(container.propertyRepository),
                    SearchPropertiesUseCase(container.propertyRepository)
                )
            }
            val category = backStackEntry.arguments?.getString("type") ?: "BUY"
            
            LaunchedEffect(category) {
                searchViewModel.onTransactionTypeChanged(category)
            }

            SearchScreen(
                viewModel = searchViewModel,
                onNavigateToDetail = { id ->
                    scope.launch {
                        try {
                            val property = container.propertyRepository.getPropertyDetails(id).first()
                            if (property != null) {
                                val user = authViewModel.currentUser.value
                                val currentIsPremium = (user?.isPremium ?: false) || (user?.role == "AGENT")
                                if (property.verified && !currentIsPremium) {
                                    showPaymentDialogForProperty = property
                                } else {
                                    navController.navigate("detail/$id")
                                }
                            }
                        } catch (e: Exception) {
                            navController.navigate("detail/$id")
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Property Detail view
        composable(
            route = "detail/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val detailsViewModel: PropertyDetailsViewModel = viewModel {
                PropertyDetailsViewModel(
                    GetPropertyDetailsUseCase(container.propertyRepository),
                    IsFavoriteUseCase(container.propertyRepository),
                    ToggleFavoriteUseCase(container.propertyRepository),
                    ContactOwnerUseCase(container.propertyRepository)
                )
            }
            PropertyDetailsScreen(
                propertyId = id,
                viewModel = detailsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Post Property Stepper
        composable("post") {
            val postViewModel: PostPropertyViewModel = viewModel {
                PostPropertyViewModel(
                    SavePropertyDraftUseCase(container.propertyRepository),
                    SubmitPropertyUseCase(container.propertyRepository)
                )
            }
            PostPropertyScreen(
                viewModel = postViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubmissionSuccess = {
                    navController.navigate("home") {
                        popUpTo("post") { inclusive = true }
                    }
                }
            )
        }

        // Support Ticketing & FAQs Help Center
        composable("support") {
            val supportViewModel: SupportCenterViewModel = viewModel {
                SupportCenterViewModel(
                    CreateSupportTicketUseCase(container.supportRepository),
                    GetSupportTicketsUseCase(container.supportRepository)
                )
            }
            SupportCenterScreen(
                viewModel = supportViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // User Profile
        composable("profile") {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateToSupport = {
                    navController.navigate("support")
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUpgradeClick = {
                    showPaymentGatewayOnly = true
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }

        // Settings Screen
        composable("settings") {
            SettingsScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSupport = {
                    navController.navigate("support")
                },
                onLoggedOut = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Notifications Screen
        composable("notifications") {
            NotificationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Menu Categories Screen
        composable("menu") {
            com.example.varahanest.presentation.menu.CategoriesMenuScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPostProperty = {
                    navController.navigate("post")
                },
                onNavigateToSupport = {
                    navController.navigate("support")
                },
                onCategoryClick = { searchType ->
                    navController.navigate("search?type=$searchType")
                }
            )
        }
    }

    if (showPaymentDialogForProperty != null) {
        val property = showPaymentDialogForProperty!!
        AlertDialog(
            onDismissRequest = { showPaymentDialogForProperty = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Premium Locked",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Premium Property Locked",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"${property.title}\"",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "This premium verified listing is locked. Pay ₹999 to unlock lifetime access to all premium properties.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPaymentGatewayOnly = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Pay Now to Unlock", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPaymentDialogForProperty = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Go Back", color = Color.Gray)
                }
            }
        )
    }

    if (showPaymentGatewayOnly) {
        PaymentGatewayDialog(
            onDismiss = { showPaymentGatewayOnly = false },
            onPaymentSuccess = {
                authViewModel.upgradeToPremium()
                showPaymentGatewayOnly = false
                val property = showPaymentDialogForProperty
                if (property != null) {
                    showPaymentDialogForProperty = null
                    navController.navigate("detail/${property.id}")
                }
            }
        )
    }
}

@Suppress("DEPRECATION")
@Composable
fun PaymentGatewayDialog(
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("CARD") } // CARD, UPI, NETBANK
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    Dialog(
        onDismissRequest = { if (!isProcessing && !isSuccess) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                if (!isProcessing && !isSuccess) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "SSL Secure",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Secure Checkout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Text(
                        text = "SSL 256-Bit Encrypted Transaction",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Start).padding(start = 28.dp, bottom = 16.dp)
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.0f)) {
                                Text("Varaha Premium Membership", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("One-time lifetime access", fontSize = 11.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("₹999", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Payment Method Tabs
                    TabRow(
                        selectedTabIndex = when (selectedTab) {
                            "CARD" -> 0
                            "UPI" -> 1
                            else -> 2
                        },
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = selectedTab == "CARD",
                            onClick = { selectedTab = "CARD" },
                            text = { Text("Card", fontSize = 12.sp, maxLines = 1) }
                        )
                        Tab(
                            selected = selectedTab == "UPI",
                            onClick = { selectedTab = "UPI" },
                            text = { Text("UPI", fontSize = 12.sp, maxLines = 1) }
                        )
                        Tab(
                            selected = selectedTab == "NETBANK",
                            onClick = { selectedTab = "NETBANK" },
                            text = { Text("NetBanking", fontSize = 11.sp, maxLines = 1) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tab content
                    when (selectedTab) {
                        "CARD" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { if (it.length <= 16) cardNumber = it },
                                    label = { Text("Card Number") },
                                    placeholder = { Text("1234 5678 1234 5678") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = expiry,
                                        onValueChange = { if (it.length <= 5) expiry = it },
                                        label = { Text("Expiry (MM/YY)") },
                                        placeholder = { Text("12/28") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = cvv,
                                        onValueChange = { if (it.length <= 3) cvv = it },
                                        label = { Text("CVV") },
                                        placeholder = { Text("123") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        "UPI" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = upiId,
                                    onValueChange = { upiId = it },
                                    label = { Text("UPI ID") },
                                    placeholder = { Text("username@upi") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Google Pay", "PhonePe", "Paytm").forEach { app ->
                                        OutlinedButton(
                                            onClick = { upiId = "varaha.member@okaxis" },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                        ) {
                                            Text(app, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                        "NETBANK" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Popular Banks", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("SBI", "HDFC", "ICICI", "AXIS").forEach { bank ->
                                        OutlinedButton(
                                            onClick = { 
                                                isProcessing = true
                                                scope.launch { 
                                                    delay(1500)
                                                    isProcessing = false
                                                    isSuccess = true
                                                    delay(1000)
                                                    onPaymentSuccess() 
                                                } 
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(bank, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Pay Button
                    Button(
                        onClick = {
                            isProcessing = true
                            scope.launch {
                                delay(1500)
                                isProcessing = false
                                isSuccess = true
                                delay(1000)
                                onPaymentSuccess()
                            }
                        },
                        enabled = when (selectedTab) {
                            "CARD" -> cardNumber.length >= 16 && expiry.length >= 5 && cvv.length >= 3
                            "UPI" -> upiId.contains("@")
                            else -> true
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Pay Securely ₹999", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(onClick = onDismiss) {
                        Text("Cancel Payment", color = Color.Gray, fontSize = 13.sp)
                    }
                } else if (isProcessing) {
                    // Processing screen
                    Column(
                        modifier = Modifier.padding(vertical = 32.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Processing transaction...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Please do not close the app or press back.", fontSize = 12.sp, color = Color.Gray)
                    }
                } else if (isSuccess) {
                    // Success screen
                    Column(
                        modifier = Modifier.padding(vertical = 32.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Payment Successful!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Premium Membership Activated ✨", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
