package com.example.primerproyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onAddPlatform: (String, String) -> Unit,
    onLogout: () -> Unit, // <-- Lambda para propagar el evento de cierre de sesión
    onBack: () -> Unit
) {
    var nuevaPlataforma by rememberSaveable { mutableStateOf("") }
    var mostrarConfirmacionLogout by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text("Ajustes y Plataformas", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Preferencias con DataStore
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Modo Oscuro", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Modo Oscuro activo por defecto", color = Color.Gray, fontSize = 12.sp)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                }
            }

            // 2. Alta de Plataformas (Maestra 2)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Añadir Consola / Plataforma", color = Color.White, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = nuevaPlataforma,
                        onValueChange = { nuevaPlataforma = it },
                        placeholder = { Text("Ej. Steam Deck, Xbox, PS4") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Button(
                        onClick = {
                            if (nuevaPlataforma.isNotBlank()) {
                                onAddPlatform(nuevaPlataforma, "Digital")
                                nuevaPlataforma = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Plataforma", color = Color.White)
                    }
                }
            }

            // 3. Sección de Cuenta y Cierre de Sesión
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Sesión de Usuario", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Cierra tu sesión actual y vuelve a la pantalla de acceso.", color = Color.Gray, fontSize = 12.sp)
                    Button(
                        onClick = { mostrarConfirmacionLogout = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Rojo alerta
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Diálogo de confirmación antes de salir
        if (mostrarConfirmacionLogout) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacionLogout = false },
                containerColor = CardDark,
                title = {
                    Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("¿Estás seguro de que deseas salir de tu cuenta?", color = Color.LightGray)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarConfirmacionLogout = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text("Salir", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacionLogout = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}