package com.psilva.comunicafacil.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.utils.normalizarCorreo

private const val MAX_USUARIOS = 5
class UsuariosViewModel : ViewModel() {

    private val _usuarios = mutableStateListOf<Usuario>()
    val usuarios: List<Usuario> = _usuarios

    fun registrarUsuario(nuevo: Usuario): ResultadoRegistro {
        return try {

            val correoNormalizado = nuevo.correo.normalizarCorreo()

            if (correoNormalizado.isBlank() || nuevo.clave.isBlank()) {
                return ResultadoRegistro.Error("Complete correo y contraseña")
            }

            if (!nuevo.aceptaTerminos) {
                return ResultadoRegistro.Error("Debe aceptar los términos")
            }
            if (_usuarios.size >= MAX_USUARIOS ) {
                return ResultadoRegistro.Error("Límite alcanzado: máximo 5 usuarios")
            }
            if (_usuarios.any { it.correo.normalizarCorreo() == correoNormalizado }) {
                return ResultadoRegistro.Error("Correo ya registrado")
            }

            _usuarios.add(nuevo.copy(correo = correoNormalizado))
            return ResultadoRegistro.Ok("Usuario registrado")

        }catch (e: Exception) {
            ResultadoRegistro.Error("Error inesperado al registrar usuario")
        }
    }

    fun validarLogin(correo: String, clave: String): Boolean {
        val correoNormalizado = correo.normalizarCorreo()

        if (correoNormalizado.isBlank() || clave.isBlank()) return false

        return _usuarios.any {
            it.correo == correoNormalizado && it.clave == clave
        }
    }

    fun obtenerUsuarioPorCredenciales(correo: String, clave: String): Usuario? {
        val correoNormalizado = correo.normalizarCorreo()
        if (correoNormalizado.isBlank() || clave.isBlank()) return null

        return _usuarios.firstOrNull { usuario ->
            usuario.correo == correoNormalizado && usuario.clave == clave
        }
    }

    fun existeCorreo(correo: String): Boolean {
        val correoNormalizado = correo.normalizarCorreo()
        if (correoNormalizado.isBlank()) return false

        return _usuarios
            .filter { it.correo == correoNormalizado }
            .isNotEmpty()

    }
}


sealed class ResultadoRegistro {
    data class Ok(val mensaje: String) : ResultadoRegistro()
    data class Error(val mensaje: String) : ResultadoRegistro()
}


