package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PhonoFlicksDarkColorScheme = darkColorScheme(
    primary = NeonIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = NeonCyan,
    onSecondary = CinemaBg,
    secondaryContainer = Color(0xFF0E7490),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = NeonGold,
    onTertiary = CinemaBg,
    background = CinemaBg,
    onBackground = TextPrimary,
    surface = CinemaSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun PhonoFlicksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // PhonoFlicks is purposefully styled with a dedicated midnight cinema neon aesthetic
    MaterialTheme(
        colorScheme = PhonoFlicksDarkColorScheme,
        typography = Typography,
        content = content
    )
}
