package com.example.primerproyecto.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.primerproyecto.R

data class PredefinedGame(
    val titulo: String,
    val plataformaSugerida: String,
    val colorInicio: Color,
    val colorFin: Color,
    val drawableRes: Int? = null
)

val CatalogoJuegosPredefinidos = listOf(
    PredefinedGame("Elden Ring", "PS5", Color(0xFF1B4332), Color(0xFFD4AF37),R.drawable.elden_ring),
    PredefinedGame("Baldur's Gate 3", "PC", Color(0xFF4A154B), Color(0xFFFF6B35),R.drawable.baldur_gate_3),
    PredefinedGame("Zelda: Tears of the Kingdom", "Switch", Color(0xFF0077B6), Color(0xFF90E0EF),R.drawable.zelda_tears_of_the_kingdom),
    PredefinedGame("Cyberpunk 2077", "PC", Color(0xFFF7B801), Color(0xFF7209B7),R.drawable.cyberpunk_2077),
    PredefinedGame("God of War Ragnarök", "PS5", Color(0xFF03045E), Color(0xFF00B4D8),R.drawable.god_of_war_ragnarok),
    PredefinedGame("Hollow Knight", "Switch", Color(0xFF2B2D42), Color(0xFF8D99AE),R.drawable.hollow_knight),
    PredefinedGame("Minecraft", "PC", Color(0xFF2D6A4F), Color(0xFF74C69D),R.drawable.minecraft),
    PredefinedGame("Pokémon Luminescent", "Switch", Color(0xFF3A0CA3), Color(0xFFF72585),R.drawable.pokemon_luminescent)
)

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
    var juegoAEliminar by remember { mutableStateOf<VideojuegoEntity?>(null) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = AccentPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi Colección", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = TextPrimary)
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
                            color = TextSecondary,
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
                                        labelColor = TextSecondary
                                    ),
                                    shape = CircleShape
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.videojuegos.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No hay juegos en esta vista. Pulsa '+' para elegir del catálogo.",
                                    color = TextSecondary
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
                                        onDelete = { juegoAEliminar = juego }
                                    )
                                }
                            }
                        }
                    }

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

                    juegoAEliminar?.let { juego ->
                        AlertDialog(
                            onDismissRequest = { juegoAEliminar = null },
                            containerColor = CardDark,
                            title = {
                                Text(
                                    text = "Eliminar videojuego",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = "¿Estás seguro de que deseas eliminar \"${juego.titulo}\" de tu colección? También se borrarán sus sesiones registradas.",
                                    color = TextSecondary
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteGame(juego)
                                        juegoAEliminar = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53935)
                                    )
                                ) {
                                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { juegoAEliminar = null }
                                ) {
                                    Text("Cancelar", color = TextSecondary)
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

    // Buscamos los datos visuales del juego en el catálogo predefinido
    val preset = CatalogoJuegosPredefinidos.find { it.titulo.equals(juego.titulo, ignoreCase = true) }
    val brush = Brush.linearGradient(
        colors = listOf(
            preset?.colorInicio ?: AccentPurple,
            preset?.colorFin ?: Color(0xFF252038)
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Portada del juego (Imagen si tiene drawableRes, o degradado artístico gamer)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush),
                contentAlignment = Alignment.TopEnd
            ) {
                if (preset?.drawableRes != null) {
                    Image(
                        painter = painterResource(id = preset.drawableRes),
                        contentDescription = juego.titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Icono decorativo central en la portada
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(54.dp)
                            .align(Alignment.Center)
                    )
                }

                // Etiqueta de plataforma en la esquina superior derecha
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = juego.genero,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = juego.titulo,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                        tint = TextSecondary,
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
    var juegoSeleccionado by remember { mutableStateOf(CatalogoJuegosPredefinidos.first()) }
    var plataformaSeleccionada by rememberSaveable {
        mutableStateOf(CatalogoJuegosPredefinidos.first().plataformaSugerida)
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
                    Text("Catálogo de Juegos", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                    }
                }

                Text("SELECCIONA UN VIDEOJUEGO", color = TextSecondary, fontSize = 12.sp)

                // Carrusel visual de portadas para elegir con un solo toque
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(CatalogoJuegosPredefinidos) { itemCatalogo ->
                        val isSelected = juegoSeleccionado.titulo == itemCatalogo.titulo
                        val itemBrush = Brush.linearGradient(
                            colors = listOf(itemCatalogo.colorInicio, itemCatalogo.colorFin)
                        )

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) BorderStroke(2.5.dp, AccentPurple) else null,
                            colors = CardDefaults.cardColors(containerColor = BgDark),
                            modifier = Modifier
                                .width(135.dp)
                                .clickable {
                                    juegoSeleccionado = itemCatalogo
                                    plataformaSeleccionada = itemCatalogo.plataformaSugerida
                                }
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(75.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(itemBrush),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (itemCatalogo.drawableRes != null) {
                                        Image(
                                            painter = painterResource(id = itemCatalogo.drawableRes),
                                            contentDescription = itemCatalogo.titulo,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = itemCatalogo.titulo,
                                    color = TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Text("PLATAFORMA", color = TextSecondary, fontSize = 12.sp)
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
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Text("ESTADO INICIAL", color = TextSecondary, fontSize = 12.sp)
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
                                    Text(est, color = TextPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        onConfirm(juegoSeleccionado.titulo, plataformaSeleccionada, estadoSeleccionado)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Agregar a mi colección", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}