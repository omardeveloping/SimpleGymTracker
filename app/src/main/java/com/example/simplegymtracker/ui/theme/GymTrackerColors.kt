package com.example.simplegymtracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class GymTrackerColors(
    val cardTintMint: Color,
    val cardTintSky: Color,
    val cardTintLavender: Color,
    val cardTintPeach: Color,
    val cardTintRose: Color,
    val cardTintYellow: Color,
    val cardTintCream: Color,
    val cardTintGray: Color,
    val restTimerBg: Color,
    val iconGreen: Color,
    val iconPurple: Color,
    val iconOrange: Color,
    val iconBlue: Color
)

val LocalGymTrackerColors: ProvidableCompositionLocal<GymTrackerColors> =
    staticCompositionLocalOf {
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

@Composable
fun gymTrackerColors(darkTheme: Boolean): GymTrackerColors = if (darkTheme) {
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
