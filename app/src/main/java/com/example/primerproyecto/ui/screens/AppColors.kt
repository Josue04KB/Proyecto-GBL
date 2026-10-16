package com.example.primerproyecto.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BgDark: Color
    @Composable get() = MaterialTheme.colorScheme.background

val CardDark: Color
    @Composable get() = MaterialTheme.colorScheme.surface

val TextPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

val TextSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val AccentPurple = Color(0xFF7C5CFF)
val StatusPlaying = Color(0xFF00C853)
val StatusPending = Color(0xFFFFAB00)
val StatusCompleted = Color(0xFF7C5CFF)
val StatusDropped = Color(0xFF9E9E9E)