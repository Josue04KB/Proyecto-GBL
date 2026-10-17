package com.example.primerproyecto.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.primerproyecto.data.local.SettingsDataStore
import com.example.primerproyecto.ui.screens.*
import com.example.primerproyecto.ui.viewmodel.AppViewModelProvider
import com.example.primerproyecto.ui.viewmodel.CatalogUiState
import com.example.primerproyecto.ui.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch

@Composable
fun GameNavGraph(
    settingsDataStore: SettingsDataStore,
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    val isDarkMode by settingsDataStore.isDarkMode.collectAsStateWithLifecycle(initialValue = true)

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("catalog") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("catalog") {
            CatalogRoute(
                onNavigateToDetail = { id, titulo ->
                    val encodedTitle = Uri.encode(titulo)
                    navController.navigate("detail/$id/$encodedTitle")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable(
            route = "detail/{gameId}/{gameTitle}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.IntType },
                navArgument("gameTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: 1
            val gameTitle = backStackEntry.arguments?.getString("gameTitle") ?: "Detalle"

            GameDetailRoute(
                videojuegoId = gameId,
                tituloJuego = gameTitle,
                onBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            val catalogViewModel: CatalogViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val catalogState by catalogViewModel.uiState.collectAsStateWithLifecycle()
            val listaPlataformas = (catalogState as? CatalogUiState.Success)?.plataformas ?: emptyList()

            SettingsScreenContent(
                isDarkMode = isDarkMode,
                plataformas = listaPlataformas,
                onToggleDarkMode = { enabled ->
                    scope.launch { settingsDataStore.toggleDarkMode(enabled) }
                },
                onAddPlatform = { nombre, formato ->
                    catalogViewModel.agregarPlataforma(nombre, formato)
                },
                onDeletePlatform = { plataforma ->
                    catalogViewModel.eliminarPlataforma(plataforma)
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("catalog") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}