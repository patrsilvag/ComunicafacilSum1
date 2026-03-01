package com.psilva.comunicafacil.model

data class UbicacionUsuario(
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val ciudad: String = "",
    val ultimaActualizacion: Long = System.currentTimeMillis()
)