package com.psilva.comunicafacil.data

import com.psilva.comunicafacil.model.Usuario
import com.psilva.comunicafacil.utils.normalizarCorreo

class UsuariosRepository : UsuariosDataSource {

    private val usuarios = mutableListOf<Usuario>()

    override fun obtenerUsuarios(): List<Usuario> =
        usuarios.toList()

    override fun existeCorreo(correo: String): Boolean {
        val normalizado = correo.normalizarCorreo()
        return usuarios.any { it.correo == normalizado }
    }

    override fun validarCredenciales(correo: String, clave: String): Boolean {
        val normalizado =correo.normalizarCorreo()
        return usuarios.any {
            it.correo == normalizado && it.clave == clave
        }
    }

    override fun registrarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            if (existeCorreo(usuario.correo)) {
                Result.failure(
                    IllegalArgumentException("El correo ya se encuentra registrado")
                )
            } else {
                usuarios.add(usuario)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
