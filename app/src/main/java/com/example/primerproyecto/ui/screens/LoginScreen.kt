package com.example.primerproyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.primerproyecto.R

// CONTENEDOR CON ESTADO (Stateful Route)
@Composable
fun LoginRoute(
    onLoginSuccess: (Int) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Authenticated) {
            val userId = (uiState as AuthUiState.Authenticated).usuario.id
            viewModel.limpiarEstado()
            onLoginSuccess(userId)
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onLoginClick = viewModel::iniciarSesion,
        onNavigateToRegister = {
            viewModel.limpiarEstado()
            onNavigateToRegister()
        }
    )
}

// VISTA SIN ESTADO (Stateless Content)
@Composable
fun LoginScreenContent(
    uiState: AuthUiState,
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var identificador by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

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
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Logo de GameBacklog",
                        modifier = Modifier.size(96.dp)
                    )
                }
            }

            Text("Bienvenido de nuevo", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Inicia sesión en tu colección offline", color = TextSecondary, fontSize = 14.sp)

            Card(
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("USUARIO O CORREO", color = TextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = identificador,
                        onValueChange = { identificador = it },
                        placeholder = {
                            Text("Ingresa tu usuario o correo", color = TextSecondary.copy(alpha = 0.6f))
                        },
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
                        placeholder = {
                            Text("Ingresa tu contraseña", color = TextSecondary.copy(alpha = 0.6f))
                        },
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onLoginClick(identificador, password) },
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
                            Text("Iniciar Sesión", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes una cuenta?", color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Regístrate aquí", color = AccentPurple, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}