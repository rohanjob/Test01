package com.example.varahanest.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    primaryContainer = PrimaryContainerTerra,
    secondary = SecondaryGold,
    background = Color(0xFF1E1B1A),
    surface = Color(0xFF1E1B1A),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = BackgroundWarm,
    onSurface = BackgroundWarm,
    outline = OutlineTerra,
    surfaceVariant = Color(0xFF3E3634)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTerra,
    primaryContainer = PrimaryContainerTerra,
    secondary = SecondaryGold,
    background = BackgroundWarm,
    surface = SurfaceWarm,
    onPrimary = OnPrimaryWhite,
    onSecondary = OnSecondaryTerra,
    onBackground = OnSurfaceTerra,
    onSurface = OnSurfaceTerra,
    outline = OutlineTerra,
    surfaceVariant = SurfaceVariantTerra,
    error = ErrorRed,
    onError = OnErrorWhite
)

@Composable
fun VarahaNestTheme(
  darkTheme: Boolean = false, // Force false globally to ensure the Warm Terra light scheme is used
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

