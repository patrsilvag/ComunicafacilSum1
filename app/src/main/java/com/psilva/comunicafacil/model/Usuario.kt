package com.psilva.comunicafacil.model

data class Usuario(
    val uid: String = "", // <--- Nuevo campo fundamental para el CRUD de ubicación
    val correo: String = "",
    val clave: String = "",
    val tipoUsuario: String = "",
    val aceptaTerminos: Boolean = false,
    val preferencia: String = "Lectura Normal"
)