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

// Dark theme — exactly matches web frontend [data-theme="dark"]
// bg-page:#1C1210  bg-card:#2E1F1A  bg-card-alt:#261815  border:#4A2E25
private val DarkColorScheme = darkColorScheme(
    primary = BrandCaramelDark,           // #C4875A — accent
    onPrimary = BrandDarkBrown,           // dark text on caramel button
    primaryContainer = BrandChocoCardHigh,
    onPrimaryContainer = BrandCream,
    secondary = BrandSaddle,
    onSecondary = Color(0xFFFFFFFF),
    tertiary = BrandCaramel,
    onTertiary = BrandDarkBrown,
    background = BrandChocoBg,            // #1C1210
    onBackground = BrandChocoOnBg,        // #F5EDE0
    surface = BrandChocoSurface,          // #261815
    onSurface = BrandChocoOnBg,           // #F5EDE0
    surfaceVariant = BrandChocoCard,      // #2E1F1A — used as card variant bg
    onSurfaceVariant = BrandChocoSubtext, // #E0C9B0
    surfaceTint = Color.Transparent,
    // M3 surface container ladder → maps to Card, NavBar, BottomSheet, Dropdown
    surfaceDim = BrandChocoBg,
    surfaceContainerLowest = BrandChocoBg,
    surfaceContainerLow = BrandChocoSurface,
    surfaceContainer = BrandChocoCard,          // #2E1F1A — default Card bg
    surfaceContainerHigh = BrandChocoCardHigh,  // #3D2212 — elevated
    surfaceContainerHighest = BrandChocoPopup,  // #4A2E25 — DropdownMenu / BottomSheet
    surfaceBright = BrandChocoPopup,
    outline = BrandChocoBorder,           // #4A2E25
    outlineVariant = BrandChocoSurface,   // #261815
    inverseSurface = BrandChocoCardHigh,  // Snackbar background — dark brown
    inverseOnSurface = BrandChocoOnBg,    // Snackbar text — cream white
    inversePrimary = BrandCaramelDark,    // Snackbar action color
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    scrim = Color(0xFF1A0E06),
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
