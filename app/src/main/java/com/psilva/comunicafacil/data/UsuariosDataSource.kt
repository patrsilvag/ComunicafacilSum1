package com.psilva.comunicafacil.data

import com.psilva.comunicafacil.model.Usuario

interface UsuariosDataSource {
    suspend fun registrarUsuario(usuario: Usuario): Result<Unit>
    suspend fun login(correo: String, clave: String): Result<Usuario>
    suspend fun recuperarPassword(correo: String): Result<Unit>
}