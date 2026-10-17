package com.example.primerproyecto.ui.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.primerproyecto.GameBacklogApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AuthViewModel(
                repository = gameBacklogApplication().container.gameRepository
            )
        }
        initializer {
            CatalogViewModel(
                repository = gameBacklogApplication().container.gameRepository
            )
        }
        initializer {
            val container = gameBacklogApplication().container
            GameDetailViewModel(
                repository = container.gameRepository,
                registrarSesionUseCase = container.registrarSesionUseCase
            )
        }
    }
}

fun CreationExtras.gameBacklogApplication(): GameBacklogApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as GameBacklogApplication)