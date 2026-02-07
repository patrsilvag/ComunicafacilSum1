package com.psilva.comunicafacil.data

import com.psilva.comunicafacil.model.Usuario

interface UsuariosDataSource {

    fun obtenerUsuarios(): List<Usuario>

    fun existeCorreo(correo: String): Boolean

    fun validarCredenciales(correo: String, clave: String): Boolean

    fun registrarUsuario(usuario: Usuario): Result<Unit>
}
