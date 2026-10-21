package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.ThemePreset

@Composable
fun SukunaTheme(
    preset: ThemePreset = ThemePreset.CRIMSON,
    content: @Composable () -> Unit
) {
    val primaryColor = Color(preset.primaryHex)
    val secondaryColor = Color(preset.secondaryHex)
    val bgColor = Color(preset.bgHex)
    val cardColor = Color(preset.cardHex)

    val colorScheme = if (preset.isLight) {
        lightColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = bgColor,
            surface = cardColor,
            onPrimary = Color.White,
            onBackground = Color(0xFF171B2B),
            onSurface = Color(0xFF171B2B)
        )
    } else {
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = bgColor,
            surface = cardColor,
            onPrimary = Color.White,
            onBackground = InkLight,
            onSurface = InkLight
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
