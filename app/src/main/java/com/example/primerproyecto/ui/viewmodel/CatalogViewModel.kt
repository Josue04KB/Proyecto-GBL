package com.example.primerproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.VideojuegoEntity
import com.example.primerproyecto.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    private val usuarioIdActual = 1
    private val _filtroEstado = MutableStateFlow("Todos")

    init {
        viewModelScope.launch {
            runCatching {repository.asegurarDatosIniciales()}
        }
    }

    val uiState: StateFlow<CatalogUiState> = combine(
        repository.getVideojuegosStream(usuarioIdActual),
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
    }.catch { e ->
        emit(CatalogUiState.Error(e.message ?: "Error desconocido al cargar el catálogo"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CatalogUiState.Loading
    )

    fun cambiarFiltro(nuevoFiltro: String) {
        _filtroEstado.value = nuevoFiltro
    }

    fun agregarVideojuego(titulo: String, genero: String, estado: String) {
        if (titulo.isBlank()) return
        viewModelScope.launch {
            repository.insertarVideojuego(
                VideojuegoEntity(
                    id = 0,
                    usuarioId = usuarioIdActual,
                    titulo = titulo.trim(),
                    genero = genero.ifBlank { "General" }.trim(),
                    estado = estado
                )
            )
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

    fun eliminarVideojuego(videojuego: VideojuegoEntity) {
        viewModelScope.launch {
            repository.eliminarVideojuego(videojuego)
        }
    }

}