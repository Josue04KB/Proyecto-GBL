package com.example.primerproyecto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.primerproyecto.data.local.entities.SesionJuegoEntity
import com.example.primerproyecto.ui.viewmodel.AppViewModelProvider
import com.example.primerproyecto.ui.viewmodel.DetailUiState
import com.example.primerproyecto.ui.viewmodel.GameDetailViewModel

// CONTENEDOR CON ESTADO (Stateful Route)
@Composable
fun GameDetailRoute(
    videojuegoId: Int,
    tituloJuego: String,
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(videojuegoId) {
        viewModel.cargarJuego(videojuegoId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameDetailScreenContent(
        tituloJuego = tituloJuego,
        uiState = uiState,
        onBack = onBack,
        onAddSession = { platId, minutos, notas ->
            viewModel.registrarSesion(videojuegoId, platId, minutos, notas)
        }
    )
}

// VISTA PURA SIN ESTADO (Stateless Content)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreenContent(
    tituloJuego: String,
    uiState: DetailUiState,
    onBack: () -> Unit,
    onAddSession: (Int, Int, String) -> Unit
) {
    var minutosInput by rememberSaveable { mutableStateOf("") }
    var notasInput by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text(tituloJuego, color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        }
    ) { padding ->
        when (uiState) {
            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPurple)
                }
            }
            is DetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(uiState.mensaje, color = MaterialTheme.colorScheme.error)
                }
            }
            is DetailUiState.Success -> {
                val horasTotales = String.format("%.1f", uiState.totalMinutos / 60.0)
                val primeraPlataformaId = uiState.plataformas.firstOrNull()?.id ?: 1

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardDark),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("TIEMPO TOTAL INVERTIDO", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    text = "$horasTotales hrs (${uiState.totalMinutos} min)",
                                    color = AccentPurple,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardDark),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Registrar Nueva Sesión de Juego", color = TextPrimary, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = minutosInput,
                                    onValueChange = { minutosInput = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Minutos jugados (ej. 45)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = AccentPurple
                                    )
                                )
                                OutlinedTextField(
                                    value = notasInput,
                                    onValueChange = { notasInput = it },
                                    label = { Text("Hito o avance alcanzado") },
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
                                        val min = minutosInput.toIntOrNull() ?: 0
                                        if (min > 0) {
                                            onAddSession(primeraPlataformaId, min, notasInput)
                                            minutosInput = ""
                                            notasInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Guardar Sesión", color = Color.White)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Historial de Sesiones (${uiState.sesiones.size})",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.sesiones, key = { it.id }) { sesion ->
                        SessionItemCard(sesion)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionItemCard(sesion: SesionJuegoEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sesion.notas, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("Sesión registrada offline", color = TextSecondary, fontSize = 12.sp)
            }
            Text(
                text = "+${sesion.minutosJugados} min",
                color = StatusPlaying,
                fontWeight = FontWeight.Bold
            )
        }
    }
}