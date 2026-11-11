package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = FieldGreen,
    onPrimary = Color.White,
    primaryContainer = FieldGreenContainer,
    onPrimaryContainer = FieldGreen,
    secondary = WheatGold,
    onSecondary = Color.White,
    secondaryContainer = WheatGoldLight,
    onSecondaryContainer = WheatGoldDark,
    tertiary = SkyBlue,
    onTertiary = Color.White,
    tertiaryContainer = SkyBlueLight,
    onTertiaryContainer = Color(0xFF01579B),
    background = SurfaceOffWhite,
    onBackground = TextDark,
    surface = CardSurfaceLight,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFEFF2EE),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFD0D7D2),
    outlineVariant = Color(0xFFE2E7E3)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF0A3311),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF4E2600),
    secondaryContainer = Color(0xFFE65100),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFF4FC3F7),
    onTertiary = Color(0xFF003652),
    tertiaryContainer = Color(0xFF0288D1),
    onTertiaryContainer = Color(0xFFB3E5FC),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF38473E),
    outlineVariant = Color(0xFF28332C)
)

@Composable
fun KisanSaathiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
