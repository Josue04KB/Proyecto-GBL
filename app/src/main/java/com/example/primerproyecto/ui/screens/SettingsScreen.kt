package com.example.primerproyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.primerproyecto.data.local.entities.PlataformaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    isDarkMode: Boolean,
    plataformas: List<PlataformaEntity>,
    onToggleDarkMode: (Boolean) -> Unit,
    onAddPlatform: (String, String) -> Unit,
    onDeletePlatform: (PlataformaEntity) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var nuevaPlataforma by rememberSaveable { mutableStateOf("") }
    var mostrarConfirmacionLogout by remember { mutableStateOf(false) }
    var plataformaAEliminar by remember { mutableStateOf<PlataformaEntity?>(null) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text("Ajustes y Plataformas", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
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
                        Text("Modo Oscuro Gamer", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Persistido localmente con Jetpack DataStore", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode
                    )
                }
            }

            // 2. Gestión de Plataformas (Crear y Eliminar)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Gestión de Plataformas (Maestra 2)", color = TextPrimary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = nuevaPlataforma,
                        onValueChange = { nuevaPlataforma = it },
                        placeholder = { Text("Ej. Steam Deck, Xbox, PS4", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentPurple
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
                        Text("Guardar Plataforma", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (plataformas.isNotEmpty()) {
                        HorizontalDivider(color = TextSecondary.copy(alpha = 0.2f))
                        Text(
                            text = "PLATAFORMAS REGISTRADAS (${plataformas.size})",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            plataformas.forEach { plat ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BgDark)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(plat.nombre, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                        Text("Formato: ${plat.formatoDefault}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    IconButton(
                                        onClick = { plataformaAEliminar = plat },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar plataforma",
                                            tint = Color(0xFFE53935),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Cierre de Sesión
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Sesión de Usuario", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Cierra tu sesión actual y vuelve a la pantalla de acceso.", color = TextSecondary, fontSize = 12.sp)
                    Button(
                        onClick = { mostrarConfirmacionLogout = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
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

        // Diálogo de confirmación para eliminar plataforma
        plataformaAEliminar?.let { plat ->
            AlertDialog(
                onDismissRequest = { plataformaAEliminar = null },
                containerColor = CardDark,
                title = {
                    Text("Eliminar plataforma", color = TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        "¿Deseas eliminar la plataforma \"${plat.nombre}\"? (Nota: Si ya tiene sesiones de juego vinculadas, se conservará por integridad referencial).",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeletePlatform(plat)
                            plataformaAEliminar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { plataformaAEliminar = null }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                }
            )
        }

        // Diálogo de confirmación para cerrar sesión
        if (mostrarConfirmacionLogout) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacionLogout = false },
                containerColor = CardDark,
                title = {
                    Text("Cerrar sesión", color = TextPrimary, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("¿Estás seguro de que deseas salir de tu cuenta?", color = TextSecondary)
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
                        Text("Cancelar", color = TextSecondary)
                    }
                }
            )
        }
    }
}