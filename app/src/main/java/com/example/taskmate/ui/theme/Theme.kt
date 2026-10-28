package com.example.taskmate.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF101828),
    surface = Color(0xFF1D2939),
    onPrimary = Color(0xFF172554),
    onSecondary = Color(0xFF101828),
    onTertiary = Color(0xFF101828),
    onBackground = Color(0xFFF2F4F7),
    onSurface = Color(0xFFF2F4F7)
)

private val LightColorScheme = lightColorScheme(
    primary = TaskMateBlue,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = TaskMateBackground,
    surface = TaskMateWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TaskMateText,
    onSurface = TaskMateText
)

@Composable
fun TaskMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}