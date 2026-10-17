package com.example.primerproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.VideojuegoEntity
import com.example.primerproyecto.data.repository.GameRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data class Success(
        val videojuegos: List<VideojuegoEntity>,
        val plataformas: List<PlataformaEntity>,
        val filtroActual: String
    ) : CatalogUiState
    data class Error(val mensaje: String) : CatalogUiState
}

class CatalogViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _usuarioIdActual = MutableStateFlow(1)
    private val _filtroEstado = MutableStateFlow("Todos")

    init {
        viewModelScope.launch {
            runCatching { repository.asegurarDatosIniciales() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CatalogUiState> = _usuarioIdActual.flatMapLatest { userId ->
        combine(
            repository.getVideojuegosStream(userId),
            repository.getPlataformasStream(),
            _filtroEstado
        ) { juegos, plataformas, filtro ->
            val juegosFiltrados = if (filtro == "Todos") {
                juegos
            } else {
                juegos.filter { it.estado.equals(filtro, ignoreCase = true) }
            }
            CatalogUiState.Success(
                videojuegos = juegosFiltrados,
                plataformas = plataformas,
                filtroActual = filtro
            ) as CatalogUiState
        }
    }.catch { e ->
        emit(CatalogUiState.Error(e.message ?: "Error al cargar el catálogo"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CatalogUiState.Loading
    )

    fun establecerUsuarioActivo(usuarioId: Int) {
        _usuarioIdActual.value = usuarioId
    }

    fun cambiarFiltro(nuevoFiltro: String) {
        _filtroEstado.value = nuevoFiltro
    }

    fun agregarVideojuego(titulo: String, genero: String, estado: String) {
        if (titulo.isBlank()) return
        viewModelScope.launch {
            repository.insertarVideojuego(
                VideojuegoEntity(
                    id = 0,
                    usuarioId = _usuarioIdActual.value,
                    titulo = titulo.trim(),
                    genero = genero.ifBlank { "General" }.trim(),
                    estado = estado
                )
            )
        }
    }

    fun eliminarVideojuego(videojuego: VideojuegoEntity) {
        viewModelScope.launch {
            repository.eliminarVideojuego(videojuego)
        }
    }

    fun agregarPlataforma(nombre: String, formato: String) {
        if (nombre.isBlank()) return
        viewModelScope.launch {
            repository.insertarPlataforma(
                PlataformaEntity(
                    nombre = nombre.trim(),
                    formatoDefault = formato
                )
            )
        }
    }

    fun eliminarPlataforma(plataforma: PlataformaEntity) {
        viewModelScope.launch {
            runCatching { repository.eliminarPlataforma(plataforma) }
        }
    }
}