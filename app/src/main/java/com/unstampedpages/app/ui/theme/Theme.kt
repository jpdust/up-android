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
    primary = SecondaryLight,
    onPrimary = BackgroundDark,
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimary,

    secondary = Secondary,
    onSecondary = BackgroundDark,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = OnPrimary,

    tertiary = AccentLight,
    onTertiary = OnPrimary,
    tertiaryContainer = Accent,
    onTertiaryContainer = OnPrimary,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = PrimaryDark,
    onSurfaceVariant = OnSurfaceDark,

    error = AccentLight,
    onError = OnPrimary,

    outline = SecondaryDark,
    outlineVariant = Primary
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
