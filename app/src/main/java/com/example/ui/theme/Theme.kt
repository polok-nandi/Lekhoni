package com.example.ui.theme

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
    primary = LekhoniGoldAccent,
    onPrimary = DarkSurfaceBackground,
    primaryContainer = LekhoniInkLight,
    onPrimaryContainer = Color.White,
    secondary = LekhoniGoldLight,
    onSecondary = DarkSurfaceBackground,
    background = DarkSurfaceBackground,
    onBackground = Color(0xFFE2E4E8),
    surface = DarkPaperCard,
    onSurface = Color(0xFFE2E4E8),
    surfaceVariant = DarkPaperCanvas,
    onSurfaceVariant = Color(0xFFB0B5BD),
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = LekhoniInkPrimary,
    onPrimary = Color.White,
    primaryContainer = LekhoniInkLight,
    onPrimaryContainer = Color.White,
    secondary = LekhoniGoldAccent,
    onSecondary = LekhoniInkDark,
    background = PaperBackgroundLight,
    onBackground = Color(0xFF1F2328),
    surface = PaperCardLight,
    onSurface = Color(0xFF1F2328),
    surfaceVariant = PaperCanvasBackground,
    onSurfaceVariant = Color(0xFF57606A),
    outline = PaperBorderLight
)

@Composable
fun LekhoniTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for Lekhoni's authentic document feel
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LekhoniTypography,
        content = content
    )
}
