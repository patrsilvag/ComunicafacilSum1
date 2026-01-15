package com.psilva.comunicafacil.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.psilva.comunicafacil.ui.screens.LoginScreen
import com.psilva.comunicafacil.ui.screens.RecoverScreen
import com.psilva.comunicafacil.ui.screens.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onIrARegistro  = { navController.navigate(Screen.Registro.route) },
                onIrARecuperar  = { navController.navigate(Screen.Recuperar.route) }
            )
        }

        composable(Screen.Registro.route) {
            RegisterScreen(
                onVolverLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Recuperar.route) {
            RecoverScreen(
                onVolverLogin = { navController.popBackStack() }
            )
        }
    }
}
