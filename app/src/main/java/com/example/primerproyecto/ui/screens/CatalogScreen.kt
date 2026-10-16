package com.example.primerproyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.VideojuegoEntity
import com.example.primerproyecto.ui.viewmodel.AppViewModelProvider
import com.example.primerproyecto.ui.viewmodel.CatalogUiState
import com.example.primerproyecto.ui.viewmodel.CatalogViewModel

// CONTENEDOR CON ESTADO (Stateful Route)
@Composable
fun CatalogRoute(
    onNavigateToDetail: (Int, String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: CatalogViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CatalogScreenContent(
        uiState = uiState,
        onFilterChange = viewModel::cambiarFiltro,
        onAddGame = viewModel::agregarVideojuego,
        onDeleteGame = viewModel::eliminarVideojuego,
        onGameClick = onNavigateToDetail,
        onSettingsClick = onNavigateToSettings
    )
}

// VISTA PURA SIN ESTADO (Stateless Content)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreenContent(
    uiState: CatalogUiState,
    onFilterChange: (String) -> Unit,
    onAddGame: (String, String, String) -> Unit,
    onDeleteGame: (VideojuegoEntity) -> Unit,
    onGameClick: (Int, String) -> Unit,
    onSettingsClick: () -> Unit
) {
    var showAddModal by rememberSaveable { mutableStateOf(false) }
    // Estado para guardar temporalmente el juego que se desea eliminar y mostrar la confirmación
    var juegoAEliminar by remember { mutableStateOf<VideojuegoEntity?>(null) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = AccentPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi Colección", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddModal = true },
                containerColor = AccentPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo juego")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                is CatalogUiState.Loading -> {
                    CircularProgressIndicator(
                        color = AccentPurple,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CatalogUiState.Error -> {
                    Text(
                        text = uiState.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CatalogUiState.Success -> {
                    val filtros = listOf("Todos", "En progreso", "Pendiente", "Completado", "Abandonado")

                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "${uiState.videojuegos.size} juegos en tu colección",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filtros) { filtro ->
                                val seleccionado = uiState.filtroActual == filtro
                                FilterChip(
                                    selected = seleccionado,
                                    onClick = { onFilterChange(filtro) },
                                    label = { Text(filtro) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentPurple,
                                        selectedLabelColor = Color.White,
                                        containerColor = CardDark,
                                        labelColor = Color.Gray
                                    ),
                                    shape = CircleShape
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.videojuegos.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No hay juegos en esta vista. Pulsa '+' para agregar.",
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 80.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(uiState.videojuegos, key = { it.id }) { juego ->
                                    GameCardItem(
                                        juego = juego,
                                        onClick = { onGameClick(juego.id, juego.titulo) },
                                        onDelete = { juegoAEliminar = juego } // Abre el diálogo de confirmación
                                    )
                                }
                            }
                        }
                    }

                    // Modal para crear nuevo juego
                    if (showAddModal) {
                        NewGameModal(
                            plataformas = uiState.plataformas,
                            onDismiss = { showAddModal = false },
                            onConfirm = { titulo, plataforma, estado ->
                                onAddGame(titulo, plataforma, estado)
                                showAddModal = false
                            }
                        )
                    }

                    // Ventana emergente de confirmación para eliminar juego
                    juegoAEliminar?.let { juego ->
                        AlertDialog(
                            onDismissRequest = { juegoAEliminar = null },
                            containerColor = CardDark,
                            title = {
                                Text(
                                    text = "Eliminar videojuego",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = "¿Estás seguro de que deseas eliminar \"${juego.titulo}\" de tu colección? También se borrarán sus sesiones registradas.",
                                    color = Color.LightGray
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteGame(juego)
                                        juegoAEliminar = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53935) // Rojo de alerta
                                    )
                                ) {
                                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { juegoAEliminar = null }
                                ) {
                                    Text("Cancelar", color = Color.Gray)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCardItem(
    juego: VideojuegoEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (juego.estado) {
        "En progreso" -> StatusPlaying
        "Pendiente" -> StatusPending
        "Completado" -> StatusCompleted
        else -> StatusDropped
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF252038)),
                contentAlignment = Alignment.TopEnd
            ) {
                Surface(
                    color = BgDark.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = juego.genero,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = juego.titulo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = juego.estado,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar juego",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewGameModal(
    plataformas: List<PlataformaEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var plataformaSeleccionada by rememberSaveable {
        mutableStateOf(plataformas.firstOrNull()?.nombre ?: "PC")
    }
    var estadoSeleccionado by rememberSaveable { mutableStateOf("Pendiente") }
    val estados = listOf("En progreso", "Pendiente", "Completado", "Abandonado")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nuevo juego", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                    }
                }

                Text("TÍTULO", color = Color.Gray, fontSize = 12.sp)
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    placeholder = { Text("Nombre del juego", color = Color.DarkGray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AccentPurple
                    )
                )

                Text("PLATAFORMA", color = Color.Gray, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val listaNombres = if (plataformas.isEmpty()) listOf("PC", "PS5", "Switch") else plataformas.map { it.nombre }
                    items(listaNombres) { nombrePlat ->
                        FilterChip(
                            selected = plataformaSeleccionada == nombrePlat,
                            onClick = { plataformaSeleccionada = nombrePlat },
                            label = { Text(nombrePlat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple,
                                selectedLabelColor = Color.White,
                                containerColor = BgDark,
                                labelColor = Color.Gray
                            )
                        )
                    }
                }

                Text("ESTADO", color = Color.Gray, fontSize = 12.sp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    estados.chunked(2).forEach { fila ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            fila.forEach { est ->
                                OutlinedButton(
                                    onClick = { estadoSeleccionado = est },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (estadoSeleccionado == est) AccentPurple.copy(alpha = 0.2f) else BgDark
                                    )
                                ) {
                                    Text(est, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { onConfirm(titulo, plataformaSeleccionada, estadoSeleccionado) },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Agregar juego", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}