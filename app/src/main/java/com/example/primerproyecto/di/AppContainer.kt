package com.example.primerproyecto.di

import android.content.Context
import com.example.primerproyecto.data.local.GameBacklogDB
import com.example.primerproyecto.data.repository.GameRepository
import com.example.primerproyecto.data.repository.GameRepositoryImpl


//contrato del contenedor
interface AppContainer{
    val gameRepository: GameRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer{
    // Lazy asegura que la base de datos solo se construya cuando se necesite por primera vez
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
}