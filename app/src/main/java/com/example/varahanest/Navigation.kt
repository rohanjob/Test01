package com.example.varahanest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.varahanest.domain.usecase.*
import com.example.varahanest.presentation.auth.LoginScreen
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

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as VarahaApplication
    val container = context.container

    // Explicitly configure AuthViewModel using Use Cases from AppContainer
    val authViewModel: AuthViewModel = viewModel {
        AuthViewModel(
            LoginUseCase(container.authRepository),
            RegisterUseCase(container.authRepository),
            SendOtpUseCase(container.authRepository),
            VerifyOtpUseCase(container.authRepository),
            GetCurrentUserUseCase(container.authRepository),
            LogoutUseCase(container.authRepository)
        )
    }

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
                    navController.navigate("detail/$id")
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
                    navController.navigate("detail/$id")
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
}
