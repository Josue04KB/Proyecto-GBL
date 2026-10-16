package com.example.primerproyecto.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta Modo Oscuro
private val GamerDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7C5CFF),
    background = Color(0xFF0D0B14),
    surface = Color(0xFF181524),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF9E9E9E)
)

// Paleta Modo Claro
private val GamerLightColorScheme = lightColorScheme(
    primary = Color(0xFF6242E8),
    background = Color(0xFFF4F3FA),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onBackground = Color(0xFF14121E),
    onSurface = Color(0xFF14121E),
    onSurfaceVariant = Color(0xFF5E5A70)
)

@Composable
fun PrimerProyectoTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GamerDarkColorScheme else GamerLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}