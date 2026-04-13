package com.unstampedpages.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = OnBackground,

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = OnBackground,

    tertiary = Accent,
    onTertiary = OnPrimary,
    tertiaryContainer = AccentLight,
    onTertiaryContainer = OnPrimary,

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = MapLand,
    onSurfaceVariant = OnSurface,

    error = Accent,
    onError = OnPrimary,

    outline = PrimaryDark,
    outlineVariant = PrimaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryLight,  // Gold - high contrast on dark
    onPrimary = BackgroundDark,
    primaryContainer = PrimaryContainerDark,  // Lighter brown for visibility
    onPrimaryContainer = OnBackgroundDark,  // Light text on container

    secondary = Secondary,  // Antique gold
    onSecondary = BackgroundDark,
    secondaryContainer = SecondaryContainerDark,  // Full gold for visibility
    onSecondaryContainer = BackgroundDark,  // Dark text on gold

    tertiary = AccentLight,
    onTertiary = OnPrimary,
    tertiaryContainer = Accent,
    onTertiaryContainer = OnPrimary,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,  // More visible variant
    onSurfaceVariant = OnSurfaceVariantDark,

    error = AccentLight,
    onError = OnPrimary,

    outline = OutlineDark,  // Gold outline for visibility
    outlineVariant = OutlineVariantDark  // Lighter brown outline
)

@Composable
fun UnstampedPagesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION") // Required for API < 35; no direct replacement for older APIs
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
