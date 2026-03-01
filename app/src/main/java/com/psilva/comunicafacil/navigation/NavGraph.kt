package com.psilva.comunicafacil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Importaciones de tus clases
import com.psilva.comunicafacil.ui.screens.HomeScreen
import com.psilva.comunicafacil.ui.screens.LoginScreen
import com.psilva.comunicafacil.ui.screens.RecoverScreen
import com.psilva.comunicafacil.ui.screens.RegisterScreen
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.viewmodel.UbicacionViewModel
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel
import com.psilva.comunicafacil.model.Usuario

@Composable
fun NavGraph(
    navController: NavHostController,
    usuariosViewModel: UsuariosViewModel,
    onFontSizeModeChange: (FontSizeMode) -> Unit,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onIrARegistro = { navController.navigate(Screen.Registro.route) },
                onIrARecuperar = { navController.navigate(Screen.Recuperar.route) },
                onLoginExitoso = { usuario ->
                    val modo = if (usuario.preferencia.equals("Lectura Aumentada", ignoreCase = true)) {
                        FontSizeMode.Aumentada
                    } else {
                        FontSizeMode.Normal
                    }
                    onFontSizeModeChange(modo)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                usuariosViewModel = usuariosViewModel,
                onFontSizeModeChange = onFontSizeModeChange,
                darkMode = darkMode,
                onDarkModeChange = onDarkModeChange
            )
        }

        composable(Screen.Registro.route) {
            RegisterScreen(
                onRegistroExitoso = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onVolverLogin = {
                    usuariosViewModel.limpiarMensaje()
                    navController.popBackStack()
                },
                usuariosViewModel = usuariosViewModel,
                onFontSizeModeChange = onFontSizeModeChange
            )
        }

        composable(Screen.Recuperar.route) {
            RecoverScreen(
                onVolverLogin = {
                    usuariosViewModel.limpiarMensaje()
                    navController.popBackStack()
                },
                usuariosViewModel = usuariosViewModel
            )
        }

        composable(Screen.Home.route) {
            // Observamos el estado para obtener el usuario logueado y su UID
            val uiState by usuariosViewModel.uiState.collectAsState()

            // Si 'uid' sigue marcando error, realiza Build -> Clean Project.
            // Ya verificamos que tu clase Usuario.kt lo tiene.
            val uidActual = uiState.usuarioLogueado?.uid ?: ""

            // Creación correcta del ViewModel con Factory para pasar el UID
            val ubicacionViewModel: UbicacionViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return UbicacionViewModel(userId = uidActual) as T
                    }
                }
            )

            HomeScreen(
                onCerrarSesion = {
                    // CRUD: DELETE (Eliminar ubicación al salir por privacidad)
                    ubicacionViewModel.borrarRastroUbicacion()

                    usuariosViewModel.limpiarMensaje()
                    onFontSizeModeChange(FontSizeMode.Normal)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                ubicacionViewModel = ubicacionViewModel
            )
        }
    }
}