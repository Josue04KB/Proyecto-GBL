package com.example.primerproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.primerproyecto.data.local.SettingsDataStore
import com.example.primerproyecto.ui.navigation.GameNavGraph
import com.example.primerproyecto.ui.theme.PrimerProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsDataStore = remember { SettingsDataStore(applicationContext) }
            val isDarkMode by settingsDataStore.isDarkMode.collectAsStateWithLifecycle(initialValue = true)

            PrimerProyectoTheme(darkTheme = isDarkMode) {
                GameNavGraph(settingsDataStore = settingsDataStore)
            }
        }
    }
}