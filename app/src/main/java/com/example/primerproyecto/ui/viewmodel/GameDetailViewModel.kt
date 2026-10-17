package com.example.primerproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.SesionJuegoEntity
import com.example.primerproyecto.data.repository.GameRepository
import com.example.primerproyecto.usecase.RegistrarSesionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(
        val sesiones: List<SesionJuegoEntity>,
        val totalMinutos: Int,
        val plataformas: List<PlataformaEntity>
    ) : DetailUiState
    data class Error(val mensaje: String) : DetailUiState
}

class GameDetailViewModel(
    private val repository: GameRepository,
    private val registrarSesionUseCase: RegistrarSesionUseCase // <-- Inyectado desde la Capa de Dominio
) : ViewModel() {

    private val _videojuegoId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailUiState> = _videojuegoId.flatMapLatest { id ->
        if (id == null) {
            flowOf(DetailUiState.Loading)
        } else {
            combine(
                repository.getSesionesStream(id),
                repository.getTotalMinutosStream(id),
                repository.getPlataformasStream()
            ) { sesiones, totalMinutos, plataformas ->
                DetailUiState.Success(
                    sesiones = sesiones,
                    totalMinutos = totalMinutos ?: 0,
                    plataformas = plataformas
                ) as DetailUiState
            }.catch { e ->
                emit(DetailUiState.Error(e.message ?: "Error al cargar sesiones"))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailUiState.Loading
    )

    fun cargarJuego(videojuegoId: Int) {
        _videojuegoId.value = videojuegoId
    }

    fun registrarSesion(
        videojuegoId: Int,
        plataformaId: Int,
        minutosJugados: Int,
        notas: String
    ) {
        viewModelScope.launch {
            // Invocación directa gracias a operator fun invoke()
            registrarSesionUseCase(
                videojuegoId = videojuegoId,
                plataformaId = plataformaId,
                minutosJugados = minutosJugados,
                notas = notas
            )
        }
    }
}