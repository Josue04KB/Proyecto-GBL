package com.example.primerproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.primerproyecto.data.local.SettingsDataStore
import com.example.primerproyecto.ui.navigation.GameNavGraph
import com.example.primerproyecto.ui.theme.PrimerProyectoTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val settingsDataStore = remember { SettingsDataStore(applicationContext) }
            val isDarkMode by settingsDataStore.isDarkMode.collectAsStateWithLifecycle(initialValue = true)

            PrimerProyectoTheme(darkTheme = isDarkMode) {
                GameNavGraph(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    settingsDataStore = settingsDataStore
                )
            }
        }
    }
}