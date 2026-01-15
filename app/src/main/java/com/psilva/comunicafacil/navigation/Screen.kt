package com.psilva.comunicafacil.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Registro : Screen("registro")
    data object Recuperar : Screen("recuperar")
}

data object Home : Screen("home")
