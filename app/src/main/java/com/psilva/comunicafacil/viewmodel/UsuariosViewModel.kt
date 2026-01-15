package com.psilva.comunicafacil.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.psilva.comunicafacil.model.Usuario

class UsuariosViewModel : ViewModel() {

    private val _usuarios = mutableStateListOf<Usuario>()
    val usuarios: List<Usuario> = _usuarios

    fun registrarUsuario(nuevo: Usuario): ResultadoRegistro {
        val correoNormalizado = nuevo.correo.trim().lowercase()

        if (correoNormalizado.isBlank() || nuevo.clave.isBlank()) {
            return ResultadoRegistro.Error("Complete correo y contraseña")
        }
        if (!correoNormalizado.contains("@") || correoNormalizado.contains(" ")) {
            return ResultadoRegistro.Error("Ingrese un correo válido")
        }
        if (!nuevo.aceptaTerminos) {
            return ResultadoRegistro.Error("Debe aceptar los términos")
        }
        if (_usuarios.size >= 5) {
            return ResultadoRegistro.Error("Límite alcanzado: máximo 5 usuarios")
        }
        if (_usuarios.any { it.correo.trim().lowercase() == correoNormalizado }) {
            return ResultadoRegistro.Error("Correo ya registrado")
        }

        _usuarios.add(nuevo.copy(correo = correoNormalizado))
        return ResultadoRegistro.Ok("Usuario registrado")
    }

    fun validarLogin(correo: String, clave: String): Boolean {
        val correoNormalizado = correo.trim().lowercase()
        if (correoNormalizado.isBlank() || clave.isBlank()) return false

        return _usuarios.any {
            it.correo == correoNormalizado && it.clave == clave
        }
    }

    fun existeCorreo(correo: String): Boolean {
        val correoNormalizado = correo.trim().lowercase()
        if (correoNormalizado.isBlank()) return false

        return _usuarios.any { it.correo == correoNormalizado }
    }
}

sealed class ResultadoRegistro {
    data class Ok(val mensaje: String) : ResultadoRegistro()
    data class Error(val mensaje: String) : ResultadoRegistro()
}


