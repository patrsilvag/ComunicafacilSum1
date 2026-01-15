package com.psilva.comunicafacil.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.psilva.comunicafacil.ui.screens.HomeScreen
import com.psilva.comunicafacil.ui.screens.LoginScreen
import com.psilva.comunicafacil.ui.screens.RecoverScreen
import com.psilva.comunicafacil.ui.screens.RegisterScreen
import com.psilva.comunicafacil.viewmodel.UsuariosViewModel

@Composable
fun NavGraph(navController: NavHostController, usuariosViewModel: UsuariosViewModel) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onIrARegistro  = { navController.navigate(Screen.Registro.route) },
                onIrARecuperar  = { navController.navigate(Screen.Recuperar.route) } ,
                onLoginExitoso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                usuariosViewModel = usuariosViewModel
            )
        }

        composable(Screen.Registro.route) {
            RegisterScreen(
                onVolverLogin = { navController.popBackStack() },
                usuariosViewModel = usuariosViewModel
            )
        }

        composable(Screen.Recuperar.route) {
            RecoverScreen(
                onVolverLogin = { navController.popBackStack()},usuariosViewModel = usuariosViewModel
            )

        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
