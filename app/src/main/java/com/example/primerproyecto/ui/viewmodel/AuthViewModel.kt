package com.example.primerproyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.primerproyecto.data.local.entities.UsuarioEntity
import com.example.primerproyecto.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Authenticated(val usuario: UsuarioEntity) : AuthUiState
    data class Error(val mensaje: String) : AuthUiState
}

class AuthViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repository.asegurarDatosIniciales() }
        }
    }

    fun iniciarSesion(identificador: String, password: String) {
        if (identificador.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Por favor, completa todos los campos.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val usuario = repository.loginUsuario(identificador, password)
            if (usuario != null) {
                _uiState.value = AuthUiState.Authenticated(usuario)
            } else {
                _uiState.value = AuthUiState.Error("Credenciales incorrectas. Verifica tu usuario o contraseña.")
            }
        }
    }

    fun registrarUsuario(nombre: String, correo: String, password: String, confirmarPassword: String) {
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Todos los campos son obligatorios.")
            return
        }
        if (!correo.contains("@")) {
            _uiState.value = AuthUiState.Error("Ingresa un correo electrónico válido.")
            return
        }
        if (password.length < 4) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 4 caracteres.")
            return
        }
        if (password != confirmarPassword) {
            _uiState.value = AuthUiState.Error("Las contraseñas no coinciden.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val resultado = repository.registrarUsuario(nombre, correo, password)
            resultado.fold(
                onSuccess = { usuarioCreado ->
                    _uiState.value = AuthUiState.Authenticated(usuarioCreado)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Error al registrar usuario")
                }
            )
        }
    }

    fun limpiarEstado() {
        _uiState.value = AuthUiState.Idle
    }
}