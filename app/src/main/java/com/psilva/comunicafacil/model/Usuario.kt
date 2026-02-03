package com.psilva.comunicafacil.model

data class Usuario(
    val correo: String,
    val clave: String,
    val tipoUsuario: String,
    val aceptaTerminos: Boolean,
    val preferencia: String = "Lectura Normal"
)
