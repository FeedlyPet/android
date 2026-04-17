package com.example.feedlypet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandMidBrown,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCC2),
    onPrimaryContainer = BrandDarkBrown,
    secondary = BrandSaddle,
    onSecondary = Color(0xFFFFFFFF),
    tertiary = BrandCaramel,
    onTertiary = Color(0xFFFFFFFF),
    background = BrandCream,
    onBackground = BrandTextDark,
    surface = BrandCreamLight,
    onSurface = BrandTextDark,
    surfaceVariant = Color(0xFFEEE0CE),
    onSurfaceVariant = BrandTextMid,
    outline = Color(0xFFD4B896),
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),
)

// Chocolate dark theme — inspired by brand "Chocolate · Stacked" lockup
private val DarkColorScheme = darkColorScheme(
    primary = BrandCaramelDark,
    onPrimary = BrandDarkBrown,
    primaryContainer = BrandChocoCard,
    onPrimaryContainer = Color(0xFFFFDCC2),
    secondary = BrandSaddle,
    onSecondary = Color(0xFFFFFFFF),
    tertiary = BrandCaramel,
    onTertiary = BrandDarkBrown,
    background = BrandChocoBg,
    onBackground = BrandChocoOnBg,
    surface = BrandChocoSurface,
    onSurface = BrandChocoOnBg,
    surfaceVariant = BrandChocoCard,
    onSurfaceVariant = Color(0xFFFFFFFF).copy(alpha = 0.7f),
    outline = Color(0xFF8B5A3A),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun FeedlyPetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
