package com.psilva.comunicafacil.ui.state
import com.psilva.comunicafacil.model.Usuario
data class RegisterUiState(
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val esError: Boolean = false,
    val registroExitoso: Boolean = false,
    val usuarioLogueado: Usuario? = null
)