package com.example.primerproyecto.usecase

import com.example.primerproyecto.data.local.entities.SesionJuegoEntity
import com.example.primerproyecto.data.repository.GameRepository
import kotlinx.coroutines.flow.firstOrNull

// Caso de uso puro con operator fun invoke que valida y combina lógica antes de persistir en Room
class RegistrarSesionUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(
        videojuegoId: Int,
        plataformaId: Int,
        minutosJugados: Int,
        notas: String
    ): Result<Unit> {
        // 1. Validaciones de negocio previas a Room
        if (minutosJugados <= 0) {
            return Result.failure(IllegalArgumentException("El tiempo jugado debe ser mayor a 0 minutos."))
        }
        if (minutosJugados > 1440) {
            return Result.failure(IllegalArgumentException("Una sesión individual no puede superar las 24 horas (1440 min)."))
        }

        val notaLimpia = notas.trim().ifBlank { "Sesión completada" }

        return try {
            // 2. Lógica combinada: Si el juego estaba "Pendiente", pasa automáticamente a "En progreso"
            val juegoActual = repository.getVideojuegoPorIdStream(videojuegoId).firstOrNull()
            if (juegoActual != null && juegoActual.estado == "Pendiente") {
                repository.actualizarVideojuego(juegoActual.copy(estado = "En progreso"))
            }

            // 3. Persistir la sesión transaccional en Room
            repository.registrarSesion(
                SesionJuegoEntity(
                    id = 0,
                    videojuegoId = videojuegoId,
                    plataformaId = plataformaId,
                    fechaSesion = System.currentTimeMillis(),
                    minutosJugados = minutosJugados,
                    notas = notaLimpia
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}