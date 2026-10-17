package com.example.primerproyecto.di

import android.content.Context
import com.example.primerproyecto.data.local.GameBacklogDB
import com.example.primerproyecto.data.repository.GameRepository
import com.example.primerproyecto.data.repository.GameRepositoryImpl
import com.example.primerproyecto.usecase.RegistrarSesionUseCase

interface AppContainer {
    val gameRepository: GameRepository
    val registrarSesionUseCase: RegistrarSesionUseCase // <-- Expuesto en el contenedor
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: GameBacklogDB by lazy {
        GameBacklogDB.getDatabase(context)
    }

    override val gameRepository: GameRepository by lazy {
        GameRepositoryImpl(
            videojuegoDao = database.videojuegoDao(),
            plataformaDao = database.plataformaDao(),
            sesionJuegoDao = database.sesionJuegoDao()
        )
    }

    // Inyección limpia del Caso de Uso
    override val registrarSesionUseCase: RegistrarSesionUseCase by lazy {
        RegistrarSesionUseCase(gameRepository)
    }
}