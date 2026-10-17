package com.example.primerproyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.primerproyecto.ui.viewmodel.AppViewModelProvider
import com.example.primerproyecto.ui.viewmodel.AuthUiState
import com.example.primerproyecto.ui.viewmodel.AuthViewModel

// CONTENEDOR CON ESTADO (Stateful Route)
@Composable
fun RegisterRoute(
    onRegisterSuccess: (Int) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Authenticated) {
            val userId = (uiState as AuthUiState.Authenticated).usuario.id
            viewModel.limpiarEstado()
            onRegisterSuccess(userId)
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onRegisterClick = viewModel::registrarUsuario,
        onNavigateToLogin = {
            viewModel.limpiarEstado()
            onNavigateToLogin()
        }
    )
}

// VISTA PURA SIN ESTADO (Stateless Content)
@Composable
fun RegisterScreenContent(
    uiState: AuthUiState,
    onRegisterClick: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombreUsuario by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmarPassword by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardDark,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Registro",
                        tint = AccentPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text("Crear Cuenta", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Regístrate localmente en Room (Offline-First)", color = TextSecondary, fontSize = 14.sp)

            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("NOMBRE DE USUARIO", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = nombreUsuario,
                        onValueChange = { nombreUsuario = it },
                        placeholder = { Text("Ej. JosueGamer", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentPurple
                        )
                    )

                    Text("CORREO ELECTRÓNICO", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        placeholder = { Text("usuario@correo.com", color = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentPurple
                        )
                    )

                    Text("CONTRASEÑA", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentPurple
                        )
                    )

                    Text("CONFIRMAR CONTRASEÑA", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = confirmarPassword,
                        onValueChange = { confirmarPassword = it },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AccentPurple
                        )
                    )

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = uiState.mensaje,
                            color = Color(0xFFE53935),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            onRegisterClick(nombreUsuario, correo, password, confirmarPassword)
                        },
                        enabled = uiState !is AuthUiState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Registrarse", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes una cuenta?", color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Inicia sesión", color = AccentPurple, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}