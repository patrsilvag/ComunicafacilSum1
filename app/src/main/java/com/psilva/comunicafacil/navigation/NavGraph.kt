package com.psilva.comunicafacil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.psilva.comunicafacil.ui.screens.HomeScreen
import com.psilva.comunicafacil.ui.screens.LoginScreen
import com.psilva.comunicafacil.ui.screens.RecoverScreen
import com.psilva.comunicafacil.ui.screens.RegisterScreen
import com.psilva.comunicafacil.ui.settings.FontSizeMode
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel

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

                    val modo = if (usuario.preferencia == "Lectura Aumentada") {
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
                onVolverLogin = { navController.popBackStack() },
                usuariosViewModel = usuariosViewModel,
                onFontSizeModeChange = onFontSizeModeChange // SE AGREGA ESTA LÍNEA (Soluciona el error)
            )
        }

        composable(Screen.Recuperar.route) {
            RecoverScreen(
                onVolverLogin = { navController.popBackStack() },
                usuariosViewModel = usuariosViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCerrarSesion = {
                    // 1. Reseteamos la fuente a Normal al salir
                    onFontSizeModeChange(FontSizeMode.Normal)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}