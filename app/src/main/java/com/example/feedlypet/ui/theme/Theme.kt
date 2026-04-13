package com.example.feedlypet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PawOrangeDark,
    secondary = FurBrownDark,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBrown,
    onSecondary = DarkBrown,
    onBackground = WarmCream,
    onSurface = WarmCream,
)

private val LightColorScheme = lightColorScheme(
    primary = PawOrange,
    secondary = FurBrown,
    background = WarmCream,
    surface = WarmSurface,
    onPrimary = WarmCream,
    onSecondary = WarmCream,
    onBackground = DarkBrown,
    onSurface = DarkBrown,
)

@Composable
fun FeedlyPetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
