package com.example.simplegymtracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = CanvasWhite,
    primaryContainer = ElectricBlueDeep,
    onPrimaryContainer = CanvasWhite,
    secondary = Slate,
    onSecondary = CanvasWhite,
    tertiary = CardTintMintDark,
    onTertiary = CanvasWhite,
    background = Color(0xFF121212),
    onBackground = CanvasWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = CanvasWhite,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Muted,
    error = ErrorRed,
    onError = CanvasWhite,
    outline = HairlineStrong,
    outlineVariant = Hairline
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = CanvasWhite,
    primaryContainer = ElectricBluePressed,
    onPrimaryContainer = CanvasWhite,
    secondary = Slate,
    onSecondary = CanvasWhite,
    tertiary = CardTintMint,
    onTertiary = InkDeep,
    background = SurfaceLight,
    onBackground = Ink,
    surface = CanvasWhite,
    onSurface = Ink,
    surfaceVariant = SurfaceSoft,
    onSurfaceVariant = Slate,
    error = ErrorRed,
    onError = CanvasWhite,
    outline = Hairline,
    outlineVariant = HairlineSoft
)

@Composable
fun SimpleGymTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    val gymColors = if (darkTheme) {
        GymTrackerColors(
            cardTintMint = CardTintMintDark,
            cardTintSky = CardTintSkyDark,
            cardTintLavender = CardTintLavenderDark,
            cardTintPeach = CardTintPeachDark,
            cardTintRose = CardTintRoseDark,
            cardTintYellow = CardTintYellowDark,
            cardTintCream = CardTintCreamDark,
            cardTintGray = CardTintGrayDark,
            restTimerBg = RestTimerBgDark,
            iconGreen = IconGreenDark,
            iconPurple = IconPurpleDark,
            iconOrange = IconOrangeDark,
            iconBlue = IconBlueDark
        )
    } else {
        GymTrackerColors(
            cardTintMint = CardTintMint,
            cardTintSky = CardTintSky,
            cardTintLavender = CardTintLavender,
            cardTintPeach = CardTintPeach,
            cardTintRose = CardTintRose,
            cardTintYellow = CardTintYellow,
            cardTintCream = CardTintCream,
            cardTintGray = CardTintGray,
            restTimerBg = RestTimerBg,
            iconGreen = IconGreen,
            iconPurple = IconPurple,
            iconOrange = IconOrange,
            iconBlue = IconBlue
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalGymTrackerColors provides gymColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
