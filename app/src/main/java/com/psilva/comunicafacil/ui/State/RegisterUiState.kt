package com.psilva.comunicafacil.ui.state

data class RegisterUiState(
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val esError: Boolean = false,
    val registroExitoso: Boolean = false
)